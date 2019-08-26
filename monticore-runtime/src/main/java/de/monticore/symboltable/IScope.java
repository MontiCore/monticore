/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierSymbolPredicate;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.Splitters;

import java.util.*;

import static com.google.common.collect.FluentIterable.from;
import static de.se_rwth.commons.Joiners.DOT;
import static java.util.stream.Collectors.toSet;

public interface IScope  {

  Optional<? extends IScope> getEnclosingScopeOpt();

  /**
   * @deprecated method return type will change from optional to mandatory
   * if you want optional ues getEnclosingScope()
   */
  @Deprecated
  Optional<? extends IScope> getEnclosingScope();

  Optional<? extends IScope> isPresentEnclosingScope();
  
  /**
   * @return number of symbols directly defined/contained in this scope (not in enclosing scope).
   */
  int getSymbolsSize();
  
  /**
   * @return true, if this scope shadows symbols of the enclosing scope. By default, named scopes
   * (see #getName()) are shadowing scopes.
   */
  boolean isShadowingScope();
  
  /**
   * @return true, if this scope is spanned by a symbol. For example, a Java method spans a
   * method scope.
   */
  boolean isSpannedBySymbol();
  
  /**
   * States whether this scope exports symbols that can be used from outside the scope.
   * For example, a Java class exports symbols. In contrast, a Java if-block does not
   * export any symbols, hence, its locally defined variables cannot be referenced
   * from outside. By default, a scope with a name exports its symbols (although
   * this does not apply for Java methods).
   *
   * @return true, if this scope exports symbols that can be used from outside the scope.
   */
  boolean exportsSymbols();
  
  
  void setExportsSymbols(boolean b);
  
  void setShadowing(boolean b);
  
  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  void setAstNodeOpt(Optional<ASTNode> node);

  void setAstNodeAbsent();

  /**
   * @deprecated method return type will change from optional to mandatory
   * if you want optional ues getAstNodeOpt()
   */
  @Deprecated
  Optional<ASTNode> getAstNode();

  Optional<ASTNode> getAstNodeOpt();

  boolean isPresentAstNode();

  void setSpanningSymbolOpt(Optional<IScopeSpanningSymbol> symbol);

  void setSpanningSymbol(IScopeSpanningSymbol symbol);

  void setSpanningSymbolAbsent();

  /**
   * @deprecated method return type will change from optional to mandatory
   * if you want optional ues getSpanningSymbolOpt()
   */
  @Deprecated
  Optional<? extends IScopeSpanningSymbol> getSpanningSymbol();

  Optional<? extends IScopeSpanningSymbol> getSpanningSymbolOpt();

  boolean isPresentSpanningSymbol();
  
  /**
   * @param name of the scope
   */
  void setNameOpt(Optional<String> name);

  void setName(String name);

  void setNameAbsent();

  /**
   * @deprecated method return type will change from optional to mandatory
   * if you want optional ues getNameOpt()
   */
  @Deprecated
  Optional<String> getName();

  Optional<String> getNameOpt();

  boolean isPresentName();
  
  default String getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    return (nameParts.size() > 1) ? DOT.join(nameParts.skip(1)) : symbolName;
  }
  
  default FluentIterable<String> getNameParts(String symbolName) {
    return from(Splitters.DOT.split(symbolName));
  }
  
  default boolean checkIfContinueWithEnclosingScope(boolean foundSymbols) {
    // If this scope shadows its enclosing scope and already some symbols are found,
    // there is no need to continue searching.
    return !(foundSymbols && isShadowingScope());
  }
  
  default boolean checkIfContinueAsSubScope(String symbolName){
    if (this.exportsSymbols()) {
      final List<String> nameParts = getNameParts(symbolName).toList();
    
      if (nameParts.size() > 1) {
        final String firstNamePart = nameParts.get(0);
        // A scope that exports symbols usually has a name.
        return firstNamePart.equals(this.getNameOpt().orElse(""));
      }
    }
  
    return false;
  }
  
  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    if (resolved.size() == 1) {
      return Optional.of(resolved.iterator().next());
    }
    else if (resolved.size() > 1) {
      throw new ResolvedSeveralEntriesForSymbolException("0xA4095 Found " + resolved.size()
              + " symbols: " + resolved.iterator().next().getFullName(),
          resolved);
    }
    
    return Optional.empty();
  }
  
  default  <T extends ISymbol> Set<T> filterSymbolsByAccessModifier(AccessModifier modifier, Collection<T> resolvedUnfiltered) {
    return new LinkedHashSet<>(resolvedUnfiltered.stream().filter(new IncludesAccessModifierSymbolPredicate(modifier)).collect(toSet()));
  }
  
}
