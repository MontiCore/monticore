/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;


import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;

public class TypeVariable<ASTMCType> extends TypeExpression {

  de.monticore.types.mcbasictypes._ast.ASTMCType variableName;
  public void g() {
    ArrayList<Integer> var = Lists.newArrayList(5);
  }

  @Override
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof TypeVariable)){
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
    if(!this.variableName.deepEquals(((TypeVariable) typeExpression).variableName)){
      return false;
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
    clone.variableName = this.variableName.deepClone();
    return clone;
  }
}
