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

import java.util.Optional;

import de.monticore.ast.ASTNode;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN doc
// TODO PN use generics <T extends ASTNode> and add method createFromAST(T node)
public interface SymbolTableCreator {

  void putSpannedScopeOnStack(ScopeSpanningSymbol symbol);

  void putOnStack(MutableScope scope);

  /**
   * Adds the <code>symbol</code> to the current scope (see {@link #currentScope()}).
   *
   * Note: if <code>symbol</code> is a reference
   * (i.e., {@link de.monticore.symboltable.references.SymbolReference})
   * this method does not do anything.
   *
   * @param symbol the symbol to be added in the current scope.
   *
   * @deprecated use {@link #addToScope(Symbol)} instead
   */
  @Deprecated
  void putInScope(Symbol symbol);

  /**
   * Adds the <code>symbol</code> to the current scope (see {@link #currentScope()}).
   *
   * Note: if <code>symbol</code> is a reference
   * (i.e., {@link de.monticore.symboltable.references.SymbolReference})
   * this method does not do anything.
   *
   * @param symbol the symbol to be added in the current scope.
   */
  void addToScope(Symbol symbol);

  void setLinkBetweenSymbolAndNode(Symbol symbol, ASTNode astNode);

  void setLinkBetweenScopeAndNode(MutableScope scope, ASTNode astNode);

  /**
   * @deprecated use {@link #addToScopeAndLinkWithNode(Symbol, ASTNode)} instead
   */
  @Deprecated
  void putInScopeAndLinkWithAst(Symbol symbol, ASTNode astNode);

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
