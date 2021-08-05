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
    this.content = null;
    comp.content = null;

    return super.equalAttributes(o);
  }
}
