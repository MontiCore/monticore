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
public abstract class ASTCNodeWithSpanningSymbol<T extends ISymbol,S extends IScope> extends ASTCNodeWithSymbol<T,S>implements ASTNode/*<T,S>*/, Cloneable {


  protected Optional<S> spannedScope2 = Optional.empty();
  
  
  ////  For new SymTab #############################################################################
  
  // ----------------------------------------------------------------------
  // Handle the optional Spanned Scope
  // ----------------------------------------------------------------------
  public void setSpannedScope2(S spannedScope) {
    this.spannedScope2 = Optional.ofNullable(spannedScope);
  }
  
  public void setSpannedScopeOpt2(Optional<S> spannedScopeOpt) {
    this.spannedScope2 = spannedScopeOpt;
  }
  
  public void setSpannedScopeAbsent2() {
    this.spannedScope2 = Optional.empty();
  }
  
  public S getSpannedScope2() {
    if (getSpannedScopeOpt2().isPresent()) {
      return getSpannedScopeOpt2().get();
    }
    Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public Optional<S> getSpannedScopeOpt2() {
    return this.spannedScope2;
  }
  
  public boolean isPresentSpannedScope2() {
    return spannedScope2.isPresent();
  }

}
