/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterators.any;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierPredicate;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.visibility.IsShadowedBySymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

// TODO PN Doc resolve[Down|Up|Many|Locally] methods

/**
 * Default implementation of {@link Scope} and {@link MutableScope}.
 * Usually, all other scopes should be sub classes of this class.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class CommonScope implements MutableScope {

  private final List<Symbol> symbols = new ArrayList<>();
  private final List<MutableScope> subScopes = new ArrayList<>();

  private Boolean exportsSymbols = null;
  private Boolean isShadowingScope;

  private String name;

  protected MutableScope enclosingScope;

  private ScopeSpanningSymbol spanningSymbol;
  private ASTNode astNode;
  private Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();


  public CommonScope() {

  }

  public CommonScope(boolean isShadowingScope) {
    this(Optional.empty(), isShadowingScope);
  }

  public CommonScope(Optional<MutableScope> enclosingScope, boolean isShadowingScope) {
    Log.errorIfNull(enclosingScope);

    this.isShadowingScope = isShadowingScope;

    if (enclosingScope.isPresent()) {
      setEnclosingScope(enclosingScope.get());
    }
  }

  public CommonScope(Optional<MutableScope> enclosingScope) {
    this(enclosingScope, false);
  }

  @Override
  public void setName(final String name) {
    this.name = nullToEmpty(name);
  }

  @Override
  public Optional<MutableScope> getEnclosingScope() {
    return Optional.ofNullable(enclosingScope);
  }


  @Override
  public List<MutableScope> getSubScopes() {
    return ImmutableList.copyOf(subScopes);
  }

  public void addSubScope(MutableScope subScope) {
    if (!subScopes.contains(subScope)) {
      subScopes.add(subScope);
      subScope.setEnclosingScope(this);
    }
  }


  /**
   * Removes the sub scope <code>subScope</code>.
   * @param subScope the sub scope to be removed
   *
   */
  public void removeSubScope(MutableScope subScope) {
    if (subScopes.contains(subScope)) {
      subScopes.remove(subScope);
      subScope.setEnclosingScope(null);
    }
  }

  /**
   * @deprecated use {@link #add(Symbol)} instead
   */
  @Deprecated
  public void define(Symbol symbol) {
    add(symbol);
  }

  public void add(Symbol symbol) {
    Log.errorIfNull(symbol);

    // TODO PN is this check really needed? Some languages allow multiple definitions of symbol (names)
    if (symbols.contains(symbol)) {
      Log.warn("0xA1040 Symbol " + symbol.getName() + " (Kind: " + symbol.getKind() + ") is already "
          + "defined in scope " + getName());
    }
    symbols.add(symbol);
    symbol.setEnclosingScope(this);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMany(resolvingInfo, name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);

    final Set<T> resolved = new LinkedHashSet<>(this.<T>resolveManyLocally(resolvingInfo, name, kind));

    // TODO PN must this check be done AFTER the symbols are filtered by the access modifiers?
    continueWithEnclosingScope(resolvingInfo, name, kind, modifier, resolved);

    // filter out symbols that are not included within the access modifier
    final List<T> result = new ArrayList<>(Collections2.filter(resolved, new IncludesAccessModifierPredicate(modifier)));

    return result;
  }

  /**
   * Continues resolving with the enclosing scope.
   */
  protected <T extends Symbol> void continueWithEnclosingScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind,
      AccessModifier modifier, Set<T> alreadyResolved) {
    if (getEnclosingScope().isPresent()) {
      continueWithScope(getEnclosingScope().get(), resolvingInfo, name, kind, modifier, alreadyResolved);
    }
  }

  /**
   * Continues resolving with the given <code>scope</code>.
   */
  private <T extends Symbol> void continueWithScope(MutableScope scope, ResolvingInfo resolvingInfo, String name, SymbolKind kind,
      AccessModifier modifier, Set<T> alreadyResolved) {
    if (checkIfContinueWithResolving(!alreadyResolved.isEmpty() || resolvingInfo.areSymbolsFound())) {
      final Optional<T> resolvedFromEnclosing = scope.resolve(resolvingInfo, name, kind, modifier);

      if (resolvedFromEnclosing.isPresent()) {
        addResolvedSymbolsIfNotShadowed(alreadyResolved, resolvedFromEnclosing.get());
      }
    }
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    return resolve(new ResolvingInfo(getResolvingFilters()), name, kind, modifier);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String symbolName, SymbolKind kind) {
    return resolve(symbolName, kind, BasicAccessModifier.ABSENT);
//    return getResolvedOrThrowException(resolveMany(symbolName, kind));
  }

  protected <T extends Symbol> void addResolvedSymbolsIfNotShadowed(Collection<T> result, T resolvedSymbol) {
    // Does any local symbol shadow the symbol of the enclosing scope?
    if (!any(result.iterator(), createIsShadowingByPredicate(resolvedSymbol))) {
      result.add(resolvedSymbol);
    }
  }

  protected <T extends Symbol> void addResolvedSymbolsIfNotShadowed(Collection<T> result, Collection<T> resolvedSymbols) {
    for (T resolvedSymbol : resolvedSymbols) {
      addResolvedSymbolsIfNotShadowed(result, resolvedSymbol);
    }
  }

  /**
   * Creates the predicate that checks whether the <code>shadowedSymbol</code> (usually a symbol of
   * the enclosing scope) is shadowed by the (applied) symbols (usually the symbols of the current
   * scope).
   *
   * @return the predicate that checks symbol hiding.
   */
  protected IsShadowedBySymbol createIsShadowingByPredicate(Symbol shadowedSymbol) {
    return new IsShadowedBySymbol(shadowedSymbol);
  }


  private boolean noResolversRegistered() {
    return (resolvingFilters == null) || resolvingFilters.isEmpty();
  }



  /**
   *
   * @param targetKind the symbol targetKind
   * @return all resolvers of this scope that can resolve symbols of <code>targetKind</code>.
   */
  protected Collection<ResolvingFilter<? extends Symbol>> getResolvingFiltersForTargetKind
  (final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, final SymbolKind
      targetKind) {
    // TODO PN move this warning to the top of all resolve() methods of the Scope interface
    //    if (noResolversRegistered()) {
    //      Log.warn(BaseScope.class.getSimpleName() + "0xA1041 No resolvers registered in scope \""
    //          + getScopeName() +"\"");
    //    }


    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind = ResolvingFilter
        .getFiltersForTargetKind(resolvingFilters, targetKind);

    if (resolversForKind.isEmpty()) {
      Log.debug("No resolver found for symbol targetKind \"" + targetKind.getName() + "\" in scope \"" +
          getName() + "\"", CommonScope.class.getSimpleName());
    }

    return resolversForKind;
  }

  protected <T extends Symbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    return ResolvingFilter.getResolvedOrThrowException(resolved);
  }

  /**
   * @see Scope#resolve(SymbolPredicate)
   */
  @Override
  public Optional<? extends Symbol> resolve(SymbolPredicate predicate) {
    List<Symbol> result = new ArrayList<>(Collections2.filter(symbols, predicate));

    // TODO PN Combine with adaptors. For this: add filter(SymbolPredicate) to
    //         AdaptedResolvingFilter (maybe ResolvingFilter too). Then, run pass
    //         resolved symbols to all filters.

    continueWithEnclosingScope(predicate, result);

    return getResolvedOrThrowException(new ArrayList<>(result));
  }

  protected void continueWithEnclosingScope(SymbolPredicate predicate, List<Symbol> result) {
    if (getEnclosingScope().isPresent()) {
      continueWithScope(getEnclosingScope().get(), predicate, result);
    }
  }

  protected void continueWithScope(MutableScope scope, SymbolPredicate predicate, List<Symbol> result) {
    if (checkIfContinueWithResolving(!result.isEmpty()/* TODO PN || resolvingInfo.areSymbolsFound())*/)) {
      Optional<? extends Symbol> resolvedFromParent = scope.resolve(predicate);

      if (resolvedFromParent.isPresent()) {
        addResolvedSymbolsIfNotShadowed(result, resolvedFromParent.get());
      }
    }
  }

  /**
   * Returns true, if current scope should continue with resolving. By default,
   * if symbols are already found and the current scope is a shadowing scope,
   * the resolving process is not continued.
   *
   * @param foundSomeSymbols states whether symbols have already been found during
   *                         the current resolving process.
   * @return true, if resolving should continue
   */
  protected boolean checkIfContinueWithResolving(boolean foundSomeSymbols) {
    // If this scope shadows its enclosing scope and already some symbols are found,
    // there is no need to continue searching.
    return !(foundSomeSymbols && isShadowingScope());
  }

  @Override
  public List<Symbol> getSymbols() {
    return ImmutableList.copyOf(symbols);
  }

  @Override
  public int getSymbolsSize() {
    return symbols.size();
  }

  @Override
  public boolean isShadowingScope() {
    if (isShadowingScope == null) {
      return getName().isPresent();
    }
    return isShadowingScope;
  }

  @Override
  public Optional<String> getName() {
    if (!isNullOrEmpty(name)) {
      return Optional.of(name);
    }

    if (getSpanningSymbol().isPresent()) {
      return Optional.of(getSpanningSymbol().get().getName());
    }

    return Optional.empty();
  }

  public void setEnclosingScope(MutableScope newEnclosingScope) {
    if ((this.enclosingScope != null) && (newEnclosingScope != null)) {
      if (this.enclosingScope == newEnclosingScope) {
        return;
      }
      Log.warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
    }

    // remove this scope from current (old) enclosing scope, if exists.
    if (this.enclosingScope != null) {
      this.enclosingScope.removeSubScope(this);
    }

    // add this scope to new enclosing scope, if exists.
    if (newEnclosingScope != null) {
      newEnclosingScope.addSubScope(this);
    }

    // set new enclosing scope (or null)
    this.enclosingScope = newEnclosingScope;
  }

  @Override
  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  public void setAstNode(ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public Optional<? extends ScopeSpanningSymbol> getSpanningSymbol() {
    return Optional.ofNullable(spanningSymbol);
  }

  @Override
  public boolean isSpannedBySymbol() {
    return getSpanningSymbol().isPresent();
  }

  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    this.spanningSymbol = symbol;
  }

  @Override
  public boolean exportsSymbols() {
    if (exportsSymbols == null) {
      return getName().isPresent();
    }

    return exportsSymbols;
  }

  public void setExportsSymbols(boolean exportsSymbols) {
    this.exportsSymbols = exportsSymbols;
  }

  public void setResolvingFilters(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters = new LinkedHashSet<>(resolvingFilters);
  }

  public void addResolver(ResolvingFilter<? extends Symbol> resolvingFilter) {
    this.resolvingFilters.add(resolvingFilter);
  }

  @Override
  public Set<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableSet.copyOf(resolvingFilters);
  }

  @Override
  public String toString() {
    return symbols.toString();
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(final String name, final SymbolKind kind) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, BasicAccessModifier.ABSENT);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveLocally(String name, SymbolKind kind) {
    return getResolvedOrThrowException(
        this.<T>resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind));
  }

  // TODO PN add resolveManyLocally(String name, SymbolKind kind)

  protected <T extends Symbol> Collection<T> resolveManyLocally(ResolvingInfo resolvingInfo, String name, SymbolKind kind) {
    Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
        getResolvingFiltersForTargetKind(resolvingInfo.getResolvingFilters(), kind);

    final Set<T> resolvedSymbols = new LinkedHashSet<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {

      // TODO PN this is a quix fix. filter() should return a collection of symbols

      try {
        Optional<T> resolvedSymbol = (Optional<T>) resolvingFilter.filter(resolvingInfo, name, symbols);
        if (resolvedSymbol.isPresent()) {
          // TODO add resolvedSymbols to resolvingInfo?
          if (resolvedSymbols.contains(resolvedSymbol.get())) {
            // TODO PN do not add to list in this case?
            Log.debug("The symbol " + resolvedSymbol.get().getName() + " has already been resolved.",
                CommonScope.class.getSimpleName());
          }
          resolvedSymbols.add(resolvedSymbol.get());
        }
      }
      catch(ResolvedSeveralEntriesException e) {
        resolvedSymbols.addAll((Collection<? extends T>) e.getSymbols());
      }
    }

    // TODO PN filter shadowed symbols here?

    return resolvedSymbols;
  }

  /**
   * @see Scope#resolveLocally(SymbolKind)
   */
  @Override
  // TODO PN Test that symbols are returned in the right order
  public <T extends Symbol> List<T> resolveLocally(SymbolKind kind) {
    // TODO PN if no resolvingFilters registered, get from enclosing scope?
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
        getResolvingFiltersForTargetKind(resolvingFilters, kind);

    final Collection<T> resolvedSymbols = new LinkedHashSet<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
      final ResolvingInfo resolvingInfo = new ResolvingInfo(getResolvingFilters());
      resolvingInfo.addInvolvedScope(this);
      Collection<T> filtered = (Collection<T>) resolvingFilter.filter(resolvingInfo, symbols);
      resolvedSymbols.addAll(filtered);
    }

    return ImmutableList.copyOf(resolvedSymbols);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);

    final Set<T> resolved = new LinkedHashSet<>(this.<T>resolveManyLocally(resolvingInfo, name, kind));

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + kind.getName()
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");

    // TODO PN Doc if a symbol is found in the current scope, resolving is stopped.
    if (!resolved.isEmpty()) {
      Log.trace("END " + resolveCall + ". Found #" + resolved.size() , "");
      return resolved;
    }

    for (MutableScope scope : getSubScopes()) {
      resolved.addAll(continueWithSubScope(scope, resolvingInfo, name, kind, modifier));
    }

    Log.trace("END " + resolveCall + ". Found #" + resolved.size() , "");

    return resolved;
  }



  /**
   * @see MutableScope#resolveDown(java.lang.String, SymbolKind)
   */
  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind) {
    return getResolvedOrThrowException(this.resolveDownMany(name, kind));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind) {
    return this.resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, BasicAccessModifier.ABSENT);
  }

  /**
   * Continues resolving with the specific <b>subScope</b>
   *
   * @param subScope the sub scope with which resolving should be continued.
   * @param resolvingInfo contains resolving information, such as, the already involved scopes.
   * @param symbolName the name of the searched symbol
   * @param kind the kind of the searched symbol
   */
  protected <T extends Symbol> Collection<T> continueWithSubScope(MutableScope subScope, ResolvingInfo resolvingInfo,
      String symbolName, SymbolKind kind, AccessModifier modifier) {
    if (checkIfContinueWithSubScope(symbolName, subScope)) {

      final FluentIterable<String> nameParts = FluentIterable.from(Splitters.DOT.split(symbolName));
      String remainingSymbolName = symbolName;

      if (nameParts.size() > 1) {
        remainingSymbolName = Joiners.DOT.join(nameParts.skip(1));
      }

      return subScope.resolveDownMany(resolvingInfo, remainingSymbolName, kind, modifier);
    }

    return new ArrayList<>();
  }

  protected boolean checkIfContinueWithSubScope(String symbolName, MutableScope subScope) {
    if(subScope.exportsSymbols()) {
      final FluentIterable<String> nameParts = FluentIterable.from(Splitters.DOT.split(symbolName));

      if (nameParts.size() > 1) {
        final String firstNamePart = nameParts.get(0);
        // A scope that exports symbols should always have a name too.
        return firstNamePart.equals(subScope.getName().orElse(""));
      }
    }

    return false;
  }
}
