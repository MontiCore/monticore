/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Foundation class of all AST-classes Shouldn't be used in an implementation, all AST-classes also
 * share the interface ASTNode
 *
 */
public abstract class ASTCNodeWithSymbol<T extends ISymbol,S extends IScope> extends ASTCNodeWithScope<S>implements ASTNode/*<T,S>*/, Cloneable {
  
  protected Optional<T> symbol2 = Optional.empty();
  
  // ----------------------------------------------------------------------
  // Handle the optional Symbol
  // ----------------------------------------------------------------------
  
  public void setSymbol2(T symbol) {
    this.symbol2 = Optional.ofNullable(symbol);
  }
  
  public void setSymbolOpt2(Optional<T> enclosingSymbolOpt) {
    this.symbol2 = enclosingSymbolOpt;
  }
  
  public void setSymbolAbsent2() {
    this.symbol2 = Optional.empty();
  }
  
  public T getSymbol2() {
    if (getSymbolOpt2().isPresent()) {
      return getSymbolOpt2().get();
    }
    Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public Optional<T> getSymbolOpt2() {
    return this.symbol2;
  }
  
  public boolean isPresentSymbol2() {
    return symbol2.isPresent();
  }

}
