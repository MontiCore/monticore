/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;


import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

import java.util.Optional;

public class TypeVariable extends TypeExpression {

    // TODO BR beisst sich mit geerbtem typeSymbol
  protected Optional<TypeVarSymbol> typeVarSymbol;

  @Override
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof TypeVariable)){
      return false;
    }
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    if(!this.typeVarSymbol.equals(((TypeVariable) typeExpression).typeVarSymbol)){
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
    TypeVariable clone = new TypeVariable();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeVarSymbol = this.typeVarSymbol;
    return clone;
  }
}
