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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.references.SymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Base class for all symbol table creators.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public abstract class CommonSymbolTableCreator implements SymbolTableCreator {

  private final ResolverConfiguration resolverConfig;
  protected final Deque<MutableScope> scopeStack;

  /**
   * The first scope OTHER THAN THE GLOBAL SCOPE that has been created, i.e., added  to the scope
   * stack. This information helps to determine the top scope within this creation process.
   */
  private MutableScope firstCreatedScope;

  public CommonSymbolTableCreator(final ResolverConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    this(resolverConfig, new ArrayDeque<>());

    // TODO PN  allow enclosingScope to be null?
    putOnStack(Log.errorIfNull(enclosingScope));
  }

  public CommonSymbolTableCreator(final ResolverConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    this.scopeStack = Log.errorIfNull(scopeStack);
    this.resolverConfig = Log.errorIfNull(resolverConfig);
  }

  @Override
  public void putSpannedScopeOnStack(ScopeSpanningSymbol symbol) {
    Log.errorIfNull(symbol);
    putOnStack((MutableScope) symbol.getSpannedScope());
  }

  @Override
  public void putOnStack(MutableScope scope) {
    Log.errorIfNull(scope);
    setResolvingFiltersForScope(scope);

    if (!scope.getEnclosingScope().isPresent() && currentScope().isPresent()) {
      scope.setEnclosingScope(currentScope().get());
      currentScope().get().addSubScope(scope);
    }
    else if (scope.getEnclosingScope().isPresent() && currentScope().isPresent()) {
      if (scope.getEnclosingScope().get() != currentScope().get()) {
        Log.warn("0xA1043 The enclosing scope is not the same as the current scope on the stack.");
      }
    }

    if ((firstCreatedScope == null) && !(scope instanceof GlobalScope)) {
      firstCreatedScope = scope;
    }

    scopeStack.addLast(scope);
  }

  /**
   * Sets the resolving filters that are available for the <code>scope</code>. If no resolvers are
   * explicitly defined for the <code>scope</code>, the resolvers of the enclosing scope are used.
   *
   * @param scope the scope
   */
  private void setResolvingFiltersForScope(final MutableScope scope) {
    // Look for resolvers that are registered for that scope
    if (scope.isSpannedBySymbol()) {
      final Symbol symbol = scope.getSpanningSymbol().get();
      
      scope.setResolvingFilters(resolverConfig.getResolver(symbol));
    }

    // If no resolvers are defined for the scope, take the ones from the enclosing scope.
    if (scope.getResolvingFilters().isEmpty()) {
      if (currentScope().isPresent()) {
        final Scope enclosingScope = currentScope().get();
        scope.setResolvingFilters(enclosingScope.getResolvingFilters());
      }
      else {
        // scope is the top scope
        scope.setResolvingFilters(resolverConfig.getTopScopeResolvingFilters());
      }
      
    }
  }

  /**
   * @deprecated use {@link #addToScope(Symbol)} instead
   */
  @Override
  @Deprecated
  public void putInScope(final Symbol symbol) {
    addToScope(symbol);
  }

  @Override
  public void addToScope(Symbol symbol) {
    if (!(symbol instanceof SymbolReference)){
      if (currentScope().isPresent()) {
        currentScope().get().add(symbol);
      }
      else {
        // TODO PN add error id
        Log.warn("Symbol cannot be added to current scope, since no scope exists.");
      }
    }
  }

  @Override
  public void setLinkBetweenSymbolAndNode(Symbol symbol, ASTNode astNode) {
    // symbol -> ast
    symbol.setAstNode(astNode);

    // ast -> symbol
    astNode.setSymbol(symbol);
    astNode.setEnclosingScope(symbol.getEnclosingScope());

    // ast -> spannedScope
    if (symbol instanceof ScopeSpanningSymbol) {
      astNode.setSpannedScope(((ScopeSpanningSymbol) symbol).getSpannedScope());
    }
  }

  @Override
  public void setLinkBetweenSpannedScopeAndNode(MutableScope scope, ASTNode astNode) {
    // scope -> ast
    scope.setAstNode(astNode);

    // ast -> scope
    // TODO PN uncomment as soon as ASTNode has been updated
    // astNode.setSpannedScope(scope);
  }

  /**
   * @deprecated use {@link #addToScopeAndLinkWithNode(Symbol, ASTNode)} instead
   */
  @Override
  @Deprecated
  public void putInScopeAndLinkWithAst(Symbol symbol, ASTNode astNode) {
    addToScopeAndLinkWithNode(symbol, astNode);
  }

  @Override
  public void addToScopeAndLinkWithNode(Symbol symbol, ASTNode astNode) {
    addToScope(symbol);
    setLinkBetweenSymbolAndNode(symbol, astNode);

    if (symbol instanceof ScopeSpanningSymbol) {
      putSpannedScopeOnStack((ScopeSpanningSymbol) symbol);
    }
  }

  @Override
  public final Optional<? extends MutableScope> removeCurrentScope() {
    return Optional.of(scopeStack.pollLast());
  }
  
  @Override
  public final Optional<? extends MutableScope> currentScope() {
    return Optional.ofNullable(scopeStack.peekLast());
  }
  
  @Override
  public final Optional<? extends ScopeSpanningSymbol> currentSymbol() {
    if (currentScope().isPresent()) {
      return currentScope().get().getSpanningSymbol();
    }
    
    return Optional.empty();
  }

  @Override
  public MutableScope getFirstCreatedScope() {
    return firstCreatedScope;
  }

  @Override
  public void setEnclosingScopeOfNodes(ASTNode root) {
    EnclosingScopeOfNodesInitializer v = new EnclosingScopeOfNodesInitializer();
    v.handle(root);
  }

}
