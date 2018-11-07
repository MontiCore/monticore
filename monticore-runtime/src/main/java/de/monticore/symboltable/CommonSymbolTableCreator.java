/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.references.SymbolReference;
import de.se_rwth.commons.logging.Log;

import static de.se_rwth.commons.logging.Log.errorIfNull;
import static de.se_rwth.commons.logging.Log.warn;
import static java.util.Optional.*;

public abstract class CommonSymbolTableCreator implements SymbolTableCreator {

  private final ResolvingConfiguration resolvingConfig;
  protected Deque<MutableScope> scopeStack;

  /**
   * The first scope OTHER THAN THE GLOBAL SCOPE that has been created, i.e., added  to the scope
   * stack. This information helps to determine the top scope within this creation process.
   */
  private MutableScope firstCreatedScope;

  public CommonSymbolTableCreator(final ResolvingConfiguration resolvingConfig,
                                  final MutableScope enclosingScope) {
    this(resolvingConfig, new ArrayDeque<>());

    putOnStack(errorIfNull(enclosingScope));
  }

  public CommonSymbolTableCreator(final ResolvingConfiguration resolvingConfig,
                                  final Deque<MutableScope> scopeStack) {
    this.scopeStack = errorIfNull(scopeStack);
    this.resolvingConfig = errorIfNull(resolvingConfig);
  }

  @Override
  public void putSpannedScopeOnStack(ScopeSpanningSymbol symbol) {
    errorIfNull(symbol);
    putOnStack((MutableScope) symbol.getSpannedScope());
  }

  @Override
  public void putOnStack(MutableScope scope) {
    errorIfNull(scope);
    setResolvingFiltersForScope(scope);

    if (!scope.getEnclosingScope().isPresent() && currentScope().isPresent()) {
      scope.setEnclosingScope(currentScope().get());
      currentScope().get().addSubScope(scope);
    } else if (scope.getEnclosingScope().isPresent() && currentScope().isPresent()) {
      if (scope.getEnclosingScope().get() != currentScope().get()) {
        warn("0xA1043 The enclosing scope is not the same as the current scope on the stack.");
      }
    }

    if ((firstCreatedScope == null) && !(scope instanceof GlobalScope)) {
      firstCreatedScope = scope;
    }

    scopeStack.addLast(scope);
  }

  /**
   * Sets the resolving filters that are available for the <code>scope</code>. If no filters are
   * explicitly defined for the <code>scope</code>, the filters of the enclosing scope are used.
   *
   * @param scope the scope
   */
  private void setResolvingFiltersForScope(final MutableScope scope) {
    // Look for filters that are registered for that scope (in case it is named).
    if (scope.getName().isPresent()) {
      scope.setResolvingFilters(resolvingConfig.getFilters(scope.getName().get()));
    }

    // If no resolving filters are defined for the scope, take the ones from the enclosing scope.
    if (scope.getResolvingFilters().isEmpty()) {
      if (currentScope().isPresent()) {
        final Scope enclosingScope = currentScope().get();
        scope.setResolvingFilters(enclosingScope.getResolvingFilters());
      } else {
        // Scope is the top scope, hence, use the default filters.
        scope.setResolvingFilters(resolvingConfig.getDefaultFilters());
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
    astNode.setSpannedScope(scope);
  }

  @Override
  public void addToScope(Symbol symbol) {
    if (!(symbol instanceof SymbolReference)) {
      if (currentScope().isPresent()) {
        currentScope().get().add(symbol);
      } else {
        warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
      }
    }
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
    return of(scopeStack.pollLast());
  }

  @Override
  public final Optional<? extends MutableScope> currentScope() {
    return ofNullable(scopeStack.peekLast());
  }

  @Override
  public final Optional<? extends ScopeSpanningSymbol> currentSymbol() {
    if (currentScope().isPresent()) {
      return currentScope().get().getSpanningSymbol();
    }

    return empty();
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

  protected void setScopeStack(final Deque<MutableScope> scopeStack) {
    this.scopeStack = scopeStack;
  }

}
