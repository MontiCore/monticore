/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

public class ObjectType extends TypeExpression {
  @Override
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof ObjectType)){
      return false;
    }
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(typeExpression.typeSymbol)){
      return false;
    }
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(typeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override
  public TypeExpression deepClone() {
    ObjectType clone = new ObjectType();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);

    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    return clone;
  }

  //String name;
  //getBaseName() hinzufügen



}
