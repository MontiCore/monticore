package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.Splitters;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.collect.FluentIterable.from;
import static de.se_rwth.commons.Joiners.DOT;

public interface IScope  {
  
  
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
  
  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);
  
  
  /**
   * @param name of the scope
   */
  void setName(String name);
  Optional<String> getName();
  
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
  
  default <T extends Symbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    if (resolved.size() == 1) {
      return Optional.of(resolved.iterator().next());
    }
    else if (resolved.size() > 1) {
      throw new ResolvedSeveralEntriesException("0xA4095 Found " + resolved.size() + " symbols: " + resolved,
          resolved);
    }
    
    return Optional.empty();
  }
  
  
  
}
