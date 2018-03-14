/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;

/**
 * Provides methods for manipulating a scope.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface MutableScope extends Scope {



  <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  /**
   * @param enclosingScope the enclosing scope. In Java, for example, a class scope is the
   *                       enclosing scope of method scopes.
   */
  void setEnclosingScope(MutableScope enclosingScope);

  /**
   * Adds a sub subScope. In Java, for example, sub scopes of a class are the method scopes.
   * @param subScope the sub scope to be added.
   */
  void addSubScope(MutableScope subScope);

  /**
   * Removes given <code>subScope</code>.
   * @param subScope the sub scope to be removed
   *
   */
  void removeSubScope(MutableScope subScope);

  /**
   * @param symbol the symbol that spans this scope. For example, a Java method spans a
   * method scope.
   */
  void setSpanningSymbol(ScopeSpanningSymbol symbol);

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(Symbol symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   * @param symbol the symbol to be removed
   */
  void remove(Symbol symbol);

  /**
   * Sets the resolvers that are available in this scope. Within a simple grammarlanguage, these
   * resolvers are usually the same for all scopes of the grammarlanguage. The composing languages this
   * may vary.
   *
   * @param resolvingFilters the resolvers available in this scope.
   */
  void setResolvingFilters(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters);

  /**
   * Adds a <code>resolver</code> to this scope.
   *
   * @param resolvingFilter the resolvers available in this scope.
   */
  void addResolver(ResolvingFilter<? extends Symbol> resolvingFilter);


  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);
  

  /**
   * @param name of the scope
   */
  void setName(String name);

  <T extends Symbol> Collection<T> continueAsSubScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

}
