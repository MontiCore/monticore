/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class SymTypeConstant extends SymTypeExpression {

  /**
   * List of potential constants
   * (on purpose not implemented as enum)
   */
   public static List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double","void");

  /**
   * A typeConstant has a name
   */
  protected String constName;

  public SymTypeConstant(String constName) {
    this.constName = constName;
  }
  
  public String getConstName() {
    return constName;
  }

  public void setConstName(String constName) {
    if (primitiveTypes.contains(name)) {
      this.constName = constName;
    } else {
      throw new IllegalArgumentException("0xD3482 Only primitive types allowed (" + primitiveTypes.toString() + "), but was:" + name);
    }
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getConstName();
  }


  // --------------------------------------------------------------------------


    @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeConstant)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(symTypeExpression.typeSymbol)){
      return false;
    }
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(symTypeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeConstant clone = new SymTypeConstant();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(SymTypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    return clone;
  }
  //hier enum attr für primitive types

}
