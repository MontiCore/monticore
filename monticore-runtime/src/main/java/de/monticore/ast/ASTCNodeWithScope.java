/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Foundation class of all AST-classes Shouldn't be used in an implementation, all AST-classes also
 * share the interface ASTNode
 *
 */
public abstract class ASTCNodeWithScope<S extends IScope> extends ASTCNode implements ASTNode/*<T,S>*/, Cloneable {

  protected Optional<S> enclosingScope2 = Optional.empty();
  

  
  ////  For new SymTab #############################################################################
  
  // ----------------------------------------------------------------------
  // Handle the Optional Enclosing Scope
  // ----------------------------------------------------------------------
  
  public void setEnclosingScope2(S enclosingScope) {
    this.enclosingScope2 = Optional.ofNullable(enclosingScope);
  }
  
  public void setEnclosingScopeOpt2(Optional<S> enclosingScopeOpt) {
    this.enclosingScope2 = enclosingScopeOpt;
  }
  
  public void setEnclosingScopeAbsent2() {
    this.enclosingScope2 = Optional.empty();
  }
  
  public S getEnclosingScope2() {
    if (getEnclosingScopeOpt2().isPresent()) {
      return getEnclosingScopeOpt2().get();
    }
    Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public Optional<S> getEnclosingScopeOpt2() {
    return this.enclosingScope2;
  }
  
  public boolean isPresentEnclosingScope2() {
    return enclosingScope2.isPresent();
  }

}
