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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierPredicate;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.visibility.IsShadowedBySymbol;
import de.se_rwth.commons.logging.Log;

// TODO PN Doc resolve[Down|Up|Many|Locally] methods

/**
 * Default implementation of {@link Scope} and {@link MutableScope}.
 * Usually, all other scopes should be sub classes of this class.
 *
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN use generic for spanning symbol? That means, CommonScope<? extends ScopeSpanningSymbol>
public class CommonScope implements MutableScope {

  // TODO PN: by defauilt, a named scope should be a shadowing scope.
  private final boolean isShadowingScope;
  private final List<Symbol> symbols = new ArrayList<>();
  private final List<MutableScope> subScopes = new ArrayList<>();

  private String name;

  protected MutableScope enclosingScope;

  private ScopeSpanningSymbol spanningSymbol;
  private ASTNode astNode;
  private Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();


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
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);

    final Set<T> resolved = new LinkedHashSet<>(this.<T>resolveManyLocally(resolvingInfo, name, kind));

    // TODO PN must this check be done AFTER the symbols are filtered by the access modifiers?
    continueWithEnclosingScope(resolvingInfo, name, kind, modifier, resolved);

    // filter out symbols that are not included within the access modifier
    final List<T> result = new ArrayList<>(Collections2.filter(resolved, new IncludesAccessModifierPredicate(modifier)));

    return getResolvedOrThrowException(result);
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
    if (checkIfContinueWithScope(scope, !alreadyResolved.isEmpty() || resolvingInfo.areSymbolsFound())) {
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
  public <T extends Symbol> Optional<T> resolve(ResolvingInfo resolvingInfo, String symbolName, SymbolKind kind) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);

    final Set<T> resolved = new LinkedHashSet<>(this.<T>resolveManyLocally(resolvingInfo, symbolName, kind));

    Log.trace("START resolve(\"" + symbolName + "\", " + "\"" + kind.getName() + "\") in scope \"" +
        getName() + "\". Found #" + resolved.size() + " (local)" , "");

    continueWithEnclosingScope(resolvingInfo, symbolName, kind, resolved);

    Log.trace("END resolve(\"" + symbolName + "\", " + "\"" + kind.getName() + "\") in scope \"" +
        getName() + "\". Found #" + resolved.size() , "");
    return getResolvedOrThrowException(resolved);

  }

  /**
   * Continues resolving with enclosing scope, if it exists.
   *
   * @param resolvingInfo contains resolving information, such as, the already involved scopes.
   * @param symbolName the name of the searched symbol
   * @param kind the kind of the searched symbol
   * @param resolved the already found symbols
   */
  protected <T extends Symbol> void continueWithEnclosingScope(ResolvingInfo resolvingInfo, String symbolName,
      SymbolKind kind, Set<T> resolved) {
    if (getEnclosingScope().isPresent()) {
      continueWithScope(getEnclosingScope().get(), resolvingInfo, symbolName, kind, resolved);
    }
  }

  /**
   * Continues resolving with the specific <b>scope</b> (usually the enclosing scope).
   *
   * @param scope the scope with which resolving should be continued.
   * @param resolvingInfo contains resolving information, such as, the already involved scopes.
   * @param symbolName the name of the searched symbol
   * @param kind the kind of the searched symbol
   * @param resolved the already found symbols
   */
  protected <T extends Symbol> void continueWithScope(MutableScope scope, ResolvingInfo resolvingInfo, String symbolName,
      SymbolKind kind, Set<T> resolved) {
    if (checkIfContinueWithScope(scope, !resolved.isEmpty() || resolvingInfo.areSymbolsFound())) {
      final Optional<T> resolvedFromEnclosing = scope.resolve(resolvingInfo, symbolName, kind);

      if (resolvedFromEnclosing.isPresent()) {
        addResolvedSymbolsIfNotShadowed(resolved, resolvedFromEnclosing.get());
      }
    }
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String symbolName, SymbolKind kind) {
    return resolve(new ResolvingInfo(getResolvingFilters()), symbolName, kind);
  }

  protected <T extends Symbol> void addResolvedSymbolsIfNotShadowed(Collection<T> result, T resolved) {
    // Does any local symbol shadow the symbol of the enclosing scope?
    if (!any(result.iterator(), createIsShadowingByPredicate(resolved))) {
      result.add(resolved);
    }

    // TODO PN enclosing enclosing scope might throw an exception. Catch the exception and rethrow it with the
    //         resolved symbols from this scope.
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

    // TODO PN Combine with adaptors

    continueWithEnclosingScope(predicate, result);

    return getResolvedOrThrowException(new ArrayList<>(result));
  }

  protected void continueWithEnclosingScope(SymbolPredicate predicate, List<Symbol> result) {
    if (getEnclosingScope().isPresent()) {
      continueWithScope(getEnclosingScope().get(), predicate, result);
    }
  }

  protected void continueWithScope(MutableScope scope, SymbolPredicate predicate, List<Symbol> result) {
    if (checkIfContinueWithScope(scope, !result.isEmpty()/* TODO PN || resolvingInfo.areSymbolsFound())*/)) {
      Optional<? extends Symbol> resolvedFromParent = scope.resolve(predicate);

      if (resolvedFromParent.isPresent()) {
        addResolvedSymbolsIfNotShadowed(result, resolvedFromParent.get());
      }
    }
  }

  /**
   * Returns true, if <b>scope</b> (usually the enclosing scope) should continue
   * with resolving. By default, if symbols are already found and the current scope
   * is a shadowing scope, the resolving process is not continued.
   *
   * @param scope the scope which should continue with resolving
   * @param foundSomeSymbols states whether symbols have been already found during
   *                         the current resolving process.
   * @return true, if enclosing scope should continue with resolving.
   */
  protected boolean checkIfContinueWithScope(MutableScope scope, boolean foundSomeSymbols) {
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

  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    this.spanningSymbol = symbol;
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
    // TODO PN should resolve() use this method (not vice versa)?
    final Collection<T> resolvedSymbols = new LinkedHashSet<>();


    try {
      Optional<T> resolvedSymbol = resolve(name, kind);
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesException e) {
      resolvedSymbols.addAll((Collection<? extends T>) e.getSymbols());
    }

    return ImmutableSet.copyOf(resolvedSymbols);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveLocally(String name, SymbolKind kind) {
    return getResolvedOrThrowException(
        this.<T>resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind));
  }

  protected <T extends Symbol> Collection<T> resolveManyLocally(ResolvingInfo resolvingInfo, String name, SymbolKind kind) {
    Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
        getResolvingFiltersForTargetKind(resolvingInfo.getResolvingFilters(), kind);

    final Set<T> resolvedSymbols = new LinkedHashSet<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
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

    final List<T> resolvedSymbols = new ArrayList<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
      final ResolvingInfo resolvingInfo = new ResolvingInfo(getResolvingFilters());
      resolvingInfo.addInvolvedScope(this);
      List<T> s = (List<T>) resolvingFilter.filter(resolvingInfo, symbols);
      resolvedSymbols.addAll(s);
    }

    return ImmutableList.copyOf(resolvedSymbols);
  }

  protected <T extends Symbol> List<T> resolveDownManyLocally(final ResolvingInfo resolvingInfo,
      final String name, final SymbolKind kind) {
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind = getResolvingFiltersForTargetKind
        (resolvingInfo.getResolvingFilters(), kind);

    final List<T> resolvedSymbols = new ArrayList<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
      Optional<T> s = (Optional<T>) resolvingFilter.filter(resolvingInfo, name, symbols);
      if (s.isPresent()) {
        resolvedSymbols.add(s.get());
      }
    }

    return resolvedSymbols;
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(ResolvingInfo resolvingInfo, String name, SymbolKind kind) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);
    return getResolvedOrThrowException(this.<T>resolveDownMany(resolvingInfo, name, kind));
  }

  /**
   * @see MutableScope#resolveDown(java.lang.String, SymbolKind)
   */
  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind) {
    return this.resolveDown(new ResolvingInfo(getResolvingFilters()), name, kind);
  }

  // TODO PN Doc if a symbol is found, resolving is stopped.
  // TODO PN check argmuments name and kind
  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);

    final List<T> resolved = resolveDownManyLocally(resolvingInfo, name, kind);

    String resolveCall = "resolveDownMany(\"" + name + "\", \"" + kind.getName() + "\") in scope \"" + getName() +
            "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");

    // TODO PN Doc if a symbol is found in the current scope, resolving is stopped.
    if (!resolved.isEmpty()) {
      Log.trace("END " + resolveCall + ". Found #" + resolved.size() , "");
      return resolved;
    }

    for (MutableScope scope : getSubScopes()) {
      resolved.addAll(scope.resolveDownMany(resolvingInfo, name, kind));
    }

    Log.trace("END " + resolveCall + ". Found #" + resolved.size() , "");

    return resolved;
  }
}
