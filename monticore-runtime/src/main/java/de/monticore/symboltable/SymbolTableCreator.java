/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.references.SymbolReference;

public interface SymbolTableCreator {

  void putSpannedScopeOnStack(ScopeSpanningSymbol symbol);

  void putOnStack(MutableScope scope);

  /**
   * Adds the <code>symbol</code> to the current scope (see {@link #currentScope()}).
   * <p>
   * Note: if <code>symbol</code> is a reference
   * (i.e., {@link SymbolReference})
   * this method does not do anything.
   *
   * @param symbol the symbol to be added in the current scope.
   */
  void addToScope(Symbol symbol);

  void setLinkBetweenSymbolAndNode(Symbol symbol, ASTNode astNode);

  void setLinkBetweenSpannedScopeAndNode(MutableScope scope, ASTNode astNode);

  void addToScopeAndLinkWithNode(Symbol symbol, ASTNode astNode);

  Optional<? extends MutableScope> removeCurrentScope();

  Optional<? extends MutableScope> currentScope();

  Optional<? extends ScopeSpanningSymbol> currentSymbol();

  MutableScope getFirstCreatedScope();

  /**
   * Sets the enclosing scope for all ast nodes starting with the <code>root</code> node. A node that
   * does not have an enclosing node yet, gets the enclosing scope of its parent node.
   * <br />
   * Note that this method must be invoked <b>at the end of the symbol table creation</b>, i.e., in the
   * <code>endVisit</code> method of the root node, since it uses the enclosing scopes that
   * are already set.
   */
  void setEnclosingScopeOfNodes(ASTNode root);

}
