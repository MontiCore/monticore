/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

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
    if(!this.subTypes.equals(typeExpression.subTypes)){
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
    for(TypeExpression expr: subTypes){
      clone.addSubType(expr.deepClone());
    }
    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    return clone;
  }
  //hier enum attr für primitive types
}
