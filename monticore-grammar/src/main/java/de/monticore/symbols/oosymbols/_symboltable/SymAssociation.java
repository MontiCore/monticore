/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.se_rwth.commons.logging.Log;
import java.util.Optional;

@Deprecated
public class SymAssociation {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected Optional<CDAssociationSymbol> association = Optional.empty();
  
  protected AssocRoleSymbol left, right;
  protected boolean isAssociation, isComposition;
  
  public SymAssociation() {}
  
  public SymAssociation(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
      Optional<CDAssociationSymbol> association,
      AssocRoleSymbol left,
      AssocRoleSymbol right) {
    this(left, right);
    association.ifPresent(this::setAssociation);
  }
  
  public SymAssociation(CDAssociationSymbol association, AssocRoleSymbol left, AssocRoleSymbol right) {
    this(left, right);
    setAssociation(association);
  }
  
  public SymAssociation(AssocRoleSymbol left, AssocRoleSymbol right) {
    setLeft(left);
    setRight(right);
  }
  
  public AssocRoleSymbol getOtherRole(AssocRoleSymbol source) {
    if (source.equals(this.left)) {
      return this.right;
    } else if (source.equals(this.right)) {
      return this.left;
    } else {
      throw new RuntimeException(
          "0xCD000: unknown role, the passed role is not part of the association");
    }
  }
  
  public boolean isPresentAssociation() {
    return association.isPresent();
  }
  
  public CDAssociationSymbol getAssociation() {
    if (isPresentAssociation()) {
      return this.association.get();
    }
    Log.error("0xCD001: Association can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public AssocRoleSymbol getLeft() {
    return left;
  }
  
  public void setLeft(AssocRoleSymbol left) {
    this.left = left;
    this.left.setAssoc(this);
  }
  
  public AssocRoleSymbol getRight() {
    return right;
  }
  
  public void setRight(AssocRoleSymbol right) {
    this.right = right;
    this.right.setAssoc(this);
  }
  
  public boolean isAssociation() {
    return isAssociation;
  }
  
  public void setAssociation(CDAssociationSymbol association) {
    this.association = Optional.ofNullable(association);
    this.association.ifPresent(a -> a.setAssoc(this));
  }
  
  public void setIsAssociation(boolean association) {
    isAssociation = association;
  }
  
  public boolean isComposition() {
    return isComposition;
  }
  
  public void setIsComposition(boolean composition) {
    isComposition = composition;
  }
}
