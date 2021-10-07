/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

public  class ASTStereoValue extends ASTStereoValueTOP {

  @Override
  public boolean equalAttributes(Object o) {
    ASTStereoValue comp;
    if ((o instanceof ASTStereoValue)) {
      comp = (ASTStereoValue) o;
    } else {
      return false;
    }

    // Don't check derived attributes
    // comparing name
    if ( (this.name == null && comp.name != null)
            || (this.name != null && !this.name.equals(comp.name)) ) {
      return false;
    }
    return true;
  }
}
