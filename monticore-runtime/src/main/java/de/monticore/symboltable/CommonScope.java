/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.visibility.IsShadowedBySymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

/**
 * Default implementation of {@link Scope} and {@link MutableScope}.
 * Usually, all other scopes should be sub classes of this class.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class CommonScope implements MutableScope {

  private final Map<String, Collection<Symbol>> symbols = new LinkedHashMap<>();
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

  public void add(Symbol symbol) {
    Log.errorIfNull(symbol);

    final String symbolName = symbol.getName();
    if (!symbols.containsKey(symbolName)) {
      symbols.put(symbolName, new ArrayList<>());
    }

    symbols.get(symbolName).add(symbol);

    symbol.setEnclosingScope(this);
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

  @Override
  public void remove(Symbol symbol) {
    if (symbols.containsKey(symbol.getName())) {
      final boolean symbolRemoved = symbols.get(symbol.getName()).remove(symbol);
      if (symbolRemoved) {
        symbol.setEnclosingScope(null);
      }
    }
  }
  
  
  public <T extends Symbol> Optional<T> resolve(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMany(resolvingInfo, name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    return resolveMany(resolvingInfo, name, kind, modifier, x -> true);
  }

  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {
    final Set<T> resolvedSymbols = this.resolveManyLocally(resolvingInfo, name, kind, modifier, predicate);

    final Collection<T> resolvedFromEnclosing = continueWithEnclosingScope(resolvingInfo, name, kind, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);

    return resolvedSymbols;
  }

  protected <T extends Symbol> Set<T> filterSymbolsByAccessModifier(AccessModifier modifier, Set<T> resolvedUnfiltered) {
    return Scopes.filterSymbolsByAccessModifier(modifier, resolvedUnfiltered);
  }

  /**
   * Continues resolving with the enclosing scope.
   */
  protected <T extends Symbol> Collection<T> continueWithEnclosingScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {

    if (checkIfContinueWithEnclosingScope(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveMany(resolvingInfo, name, kind, modifier, predicate);
    }

    return Collections.emptySet();
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMany(name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolveMany(name, kind, modifier, predicate));
  }

  @Override
  public <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier) {
    return this.resolveLocally(name, kind);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveMany(name, kind, modifier,x -> true);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, Predicate<Symbol> predicate) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, AccessModifier.ALL_INCLUSION, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String symbolName, SymbolKind kind) {
    return getResolvedOrThrowException(resolveMany(symbolName, kind));
  }

  protected <T extends Symbol> boolean isNotSymbolShadowed(Collection<T> shadowingSymbols, T symbol) {
    // Does any local symbol shadow the symbol of the enclosing scope?
    return shadowingSymbols.stream().noneMatch(createIsShadowingByPredicate(symbol));
  }

  protected <T extends Symbol> Collection<T> getNotShadowedSymbols(Collection<T> shadowingSymbols, Collection<T> symbols) {
    final Collection<T> result = new LinkedHashSet<>();

    for (T resolvedSymbol : symbols) {
      if (isNotSymbolShadowed(shadowingSymbols, resolvedSymbol)) {
        result.add(resolvedSymbol);
      }
    }

    return result;
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


  /**
   *
   * @param targetKind the symbol targetKind
   * @return all resolvers of this scope that can resolve symbols of <code>targetKind</code>.
   */
  protected Collection<ResolvingFilter<? extends Symbol>> getResolvingFiltersForTargetKind
  (final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, final SymbolKind
      targetKind) {

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
   * Returns true, if current scope should continue with resolving. By default,
   * if symbols are already found and the current scope is a shadowing scope,
   * the resolving process is not continued.
   *
   * @param foundSymbols states whether symbols have already been found during
   *                         the current resolving process.
   * @return true, if resolving should continue
   */
  protected boolean checkIfContinueWithEnclosingScope(boolean foundSymbols) {
    // If this scope shadows its enclosing scope and already some symbols are found,
    // there is no need to continue searching.
    return !(foundSymbols && isShadowingScope());
  }

  @Override
  public Map<String, Collection<Symbol>> getLocalSymbols() {
    return ImmutableMap.copyOf(symbols);
  }

  @Override
  public int getSymbolsSize() {
    int size = 0;
    for (Entry<String, Collection<Symbol>> entry: symbols.entrySet()) {
      size += entry.getValue().size();
    }
    return size;
  }

  @Override
  public boolean  isShadowingScope() {
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

  @Override
  public MutableScope getAsMutableScope() {
    return this;
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
    return resolveMany(name, kind, AccessModifier.ALL_INCLUSION);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveLocally(String name, SymbolKind kind) {
    return getResolvedOrThrowException(
        this.<T>resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind, AccessModifier.ALL_INCLUSION, x -> true));
  }

  protected <T extends Symbol> Set<T> resolveManyLocally(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {
    Log.errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);
    Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
        getResolvingFiltersForTargetKind(resolvingInfo.getResolvingFilters(), kind);

    final Set<T> resolvedSymbols = new LinkedHashSet<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {

      try {
        Optional<T> resolvedSymbol = (Optional<T>) resolvingFilter.filter(resolvingInfo, name, symbols);
        if (resolvedSymbol.isPresent()) {
          if (resolvedSymbols.contains(resolvedSymbol.get())) {
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

    // filter out symbols that are not included within the access modifier
    Set<T> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    resolvingInfo.updateSymbolsFound(!filteredSymbols.isEmpty());

    return filteredSymbols;
  }

  /**
   * @see Scope#resolveLocally(SymbolKind)
   */
  @Override
  public <T extends Symbol> List<T> resolveLocally(SymbolKind kind) {
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
        getResolvingFiltersForTargetKind(resolvingFilters, kind);

    final Collection<T> resolvedSymbols = new LinkedHashSet<>();

    final Collection<Symbol> symbolsAsList = Scopes.getLocalSymbolsAsCollection(this);

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
      final ResolvingInfo resolvingInfo = new ResolvingInfo(getResolvingFilters());
      resolvingInfo.addInvolvedScope(this);
      Collection<T> filtered = (Collection<T>) resolvingFilter.filter(resolvingInfo, symbolsAsList);
      resolvedSymbols.addAll(filtered);
    }

    return ImmutableList.copyOf(resolvedSymbols);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveDownMany(name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolveDownMany(name, kind, modifier, predicate));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, x -> true);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {
    // 1. Conduct search locally in the current scope
    final Set<T> resolved = this.resolveManyLocally(resolvingInfo, name, kind, modifier, predicate);

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + kind.getName()
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (MutableScope subScope : getSubScopes()) {
        final Collection<T> resolvedFromSub = subScope.continueAsSubScope(resolvingInfo, name, kind, modifier, predicate);
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
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
    return this.resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, AccessModifier.ALL_INCLUSION, x -> true);
  }

  /**
   * Continues (top-down) resolving with this sub scope
   *
   * @param resolvingInfo contains resolving information, such as, the already involved scopes.
   * @param symbolName the name of the searched symbol
   * @param kind the kind of the searched symbol
   */
  @Override
  public <T extends Symbol> Collection<T> continueAsSubScope(ResolvingInfo resolvingInfo,
      String symbolName, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    if (checkIfContinueAsSubScope(symbolName, kind)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(symbolName);

      return this.resolveDownMany(resolvingInfo, remainingSymbolName, kind, modifier, predicate);
    }

    return Collections.emptySet();
  }

  protected boolean checkIfContinueAsSubScope(String symbolName, SymbolKind kind) {
    if(this.exportsSymbols()) {
      final List<String> nameParts = getNameParts(symbolName).toList();

      if (nameParts.size() > 1) {
        final String firstNamePart = nameParts.get(0);
        // A scope that exports symbols usually has a name.
        return firstNamePart.equals(this.getName().orElse(""));
      }
    }

    return false;
  }

  protected String getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    return (nameParts.size() > 1) ? Joiners.DOT.join(nameParts.skip(1)) : symbolName;
  }

  protected FluentIterable<String> getNameParts(String symbolName) {
    return FluentIterable.from(Splitters.DOT.split(symbolName));
  }
}
