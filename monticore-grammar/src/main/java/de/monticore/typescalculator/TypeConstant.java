/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class TypeConstant extends TypeExpression {

  @Override
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof TypeConstant)){
      return false;
    }
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(typeExpression.typeSymbol)){
      return false;
    }
    if(!this.superTypes.equals(typeExpression.superTypes)){
      return false;
    }
    return true;
  }

  @Override
  public TypeExpression deepClone() {
    TypeConstant clone = new TypeConstant();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    return clone;
  }
  //hier enum attr für primitive types

  @Override
  public void setName(String name) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double","void");
    if(primitiveTypes.contains(name)) {
      this.name = name;
    } else {
      throw new IllegalArgumentException("Only primitive types allowed ("+primitiveTypes.toString()+") but was:"+ name);
    }
  }

}
