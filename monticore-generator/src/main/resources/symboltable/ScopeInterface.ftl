<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName", "symbolNames", "superScopes", "scopeName", "scopeRule")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import java.util.function.Predicate;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import com.google.common.collect.LinkedListMultimap;
import java.util.LinkedHashSet;

import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import de.monticore.utils.Names;
import de.monticore.symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;

public interface ${interfaceName} <#if superScopes?size != 0>extends ${superScopes?join(", ")} <#else> extends IScope</#if>  {

<#if scopeRule.isPresent()>
${includeArgs("symboltable.ScopeRuleGetSet", scopeRule.get(), true)}
</#if>
<#list symbolNames?keys as symbol>

  public boolean is${symbol}SymbolAlreadyResolved();

  public void set${symbol}SymbolAlreadyResolved(boolean symbolAlreadyResolved);

  // all resolve Methods for ${symbol}Symbol
  default public Optional<${symbolNames[symbol]}> resolve${symbol}(String name) {
    return getResolvedOrThrowException(resolve${symbol}Many(name));
  }

  default public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolve${symbol}Many(name, modifier));
  }

  default public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate){
    return getResolvedOrThrowException(resolve${symbol}Many(name, modifier, predicate));
  }

  default public Optional<${symbolNames[symbol]}> resolve${symbol}(boolean foundSymbols, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolve${symbol}Many(foundSymbols, name, modifier));
  }

  // all resolveDown Methods for ${symbol}Symbol
  default public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name) {
    return getResolvedOrThrowException(this.resolve${symbol}DownMany(name));
  }

  default public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolve${symbol}DownMany(name, modifier));
  }

  default public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate) {
    return getResolvedOrThrowException(resolve${symbol}DownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for ${symbol}Symbol
  default public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name) {
    return this.resolve${symbol}DownMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier) {
    return resolve${symbol}DownMany(false, name, modifier, x -> true);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate) {
    return resolve${symbol}DownMany(false, name, modifier, predicate);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate) {
    if (!is${symbol}SymbolAlreadyResolved()) {
      set${symbol}SymbolAlreadyResolved(true);
    } else {
      return new LinkedHashSet<>();
    }

    // 1. Conduct search locally in the current scope
    final Set<${symbolNames[symbol]}> resolved = this.resolve${symbol}LocallyMany(foundSymbols, name,
        modifier, predicate);

    foundSymbols = foundSymbols | resolved.size() > 0;

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "${symbolNames[symbol]}"
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (${interfaceName} subScope : getSubScopes()) {
        final Collection<${symbolNames[symbol]}> resolvedFromSub = subScope
            .continueAs${symbol}SubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");
    set${symbol}SymbolAlreadyResolved(false);
    return resolved;
  }

  // resolveLocally Method for ${symbol}Symbol
  default public Optional<${symbolNames[symbol]}> resolve${symbol}Locally(String name) {
    return getResolvedOrThrowException(
        this.resolve${symbol}LocallyMany(false, name,  AccessModifier.ALL_INCLUSION, x -> true));
  }

  // all resolveImported Methods for ${symbol}Symbol
  default public Optional<${symbolNames[symbol]}> resolve${symbol}Imported(String name, AccessModifier modifier) {
    return this.resolve${symbol}Locally(name);
  }

  // all resolveMany Methods for ${symbol}Symbol
  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name) {
    return resolve${symbol}Many(name, AccessModifier.ALL_INCLUSION);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier) {
    return resolve${symbol}Many(name, modifier, x -> true);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate)  {
    return resolve${symbol}Many(false, name, modifier, predicate);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, Predicate<${symbolNames[symbol]}> predicate)  {
    return resolve${symbol}Many(false, name, AccessModifier.ALL_INCLUSION, predicate);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(boolean foundSymbols, String name, AccessModifier modifier) {
    return resolve${symbol}Many(foundSymbols, name, modifier, x -> true);
  }

  default public Collection<${symbolNames[symbol]}> resolve${symbol}Many(boolean foundSymbols, String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate)  {
    if (!is${symbol}SymbolAlreadyResolved()) {
      set${symbol}SymbolAlreadyResolved(true);
    } else {
      return new LinkedHashSet<>();
    }

    final Set<${symbolNames[symbol]}> resolvedSymbols = this.resolve${symbol}LocallyMany(foundSymbols, name, modifier, predicate);
    if (!resolvedSymbols.isEmpty()) {
      set${symbol}SymbolAlreadyResolved(false);
      return resolvedSymbols;
    }
    resolvedSymbols.addAll(resolveAdapted${symbol}LocallyMany(foundSymbols, name, modifier, predicate));
    if (!resolvedSymbols.isEmpty()) {
      set${symbol}SymbolAlreadyResolved(false);
      return resolvedSymbols;
    }
    final Collection<${symbolNames[symbol]}> resolvedFromEnclosing = continue${symbol}WithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    set${symbol}SymbolAlreadyResolved(false);
    return resolvedSymbols;
  }

  // method for embedding
  default public Collection<${symbolNames[symbol]}> resolveAdapted${symbol}LocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate){
    //todo: implement for embedding
    return new ArrayList<>();
  }

  default Set<${symbolNames[symbol]}> resolve${symbol}LocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<${symbolNames[symbol]}> predicate) {
    final Set<${symbolNames[symbol]}> resolvedSymbols = new LinkedHashSet<>();

    try {
      // TODO remove filter?
      Optional<${symbolNames[symbol]}> resolvedSymbol = filter${symbol}(name,
          get${symbol}Symbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // filter out symbols that are not included within the access modifier
    Set<${symbolNames[symbol]}> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(
        filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    return filteredSymbols;
  }

  default Optional<${symbolNames[symbol]}> filter${symbol}(String name, LinkedListMultimap<String, ${symbolNames[symbol]}> symbols) {
    final Set<${symbolNames[symbol]}> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (${symbolNames[symbol]} symbol : symbols.get(simpleName)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }


  default Collection<${symbolNames[symbol]}> continue${symbol}WithEnclosingScope(boolean foundSymbols, String name,  AccessModifier modifier,
      Predicate<${symbolNames[symbol]}> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolve${symbol}Many(foundSymbols, name, modifier, predicate);
    }
    return Collections.emptySet();
  }

  default Collection<${symbolNames[symbol]}> continueAs${symbol}SubScope(boolean foundSymbols, String name, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate){
    set${symbol}SymbolAlreadyResolved(false);
    if (checkIfContinueAsSubScope(name)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(name);
      return this.resolve${symbol}DownMany(foundSymbols, remainingSymbolName, modifier, predicate);
    }
    return Collections.emptySet();
  }

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(${symbolNames[symbol]} symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   *
   * @param symbol the symbol to be removed
   */
  void remove(${symbolNames[symbol]} symbol);

  default public List<${symbolNames[symbol]}> getLocal${symbol}Symbols() {
    return get${symbol}Symbols().values();
  }

  LinkedListMultimap<String, ${symbolNames[symbol]}> get${symbol}Symbols();

</#list>

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "ScopeVisitor")>
  public void accept(${langVisitorType} visitor);


  public Optional<? extends I${scopeName}Scope> getEnclosingScope();

  default public void setEnclosingScope(I${scopeName}Scope enclosingScope) {
    Log.error("0xA7012${genHelper.getGeneratedErrorCode(ast)} The method \"setEnclosingScope\" of interface \"${interfaceName}\" is not implemented.");
  }


  /**
   * Adds a sub subScope. In Java, for example, sub scopes of a class are the method scopes.
   *
   * @param subScope the sub scope to be added.
   */
  default void addSubScope(I${scopeName}Scope subScope) {
    Log.error("0xA7013${genHelper.getGeneratedErrorCode(ast)} The method \"addSubScope\" of interface \"${interfaceName}\" is not implemented.");
  }

  /**
   * Removes given <code>subScope</code>.
   *
   * @param subScope the sub scope to be removed
   */
  default void removeSubScope(I${scopeName}Scope subScope) {
    Log.error("0xA7014${genHelper.getGeneratedErrorCode(ast)} The method \"removeSubScope\" of interface \"${interfaceName}\" is not implemented.");
  }

  public Collection<? extends I${scopeName}Scope> getSubScopes();
}

