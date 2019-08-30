/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

import java.util.Optional;

public class TypeVariable extends TypeExpression {

  /**
   * A typeVariable has a name
   */
  protected String varName;

  // TODO BR: The type variable may be bounded; then it probably helps to connect to
  // the Var-Symbol that contains the bounf
  
  public TypeVariable(String varName) {
    this.varName = varName;
  }

  /**
   * We could connect the Variable to a symbol carrying that variable
   * (clarify if that is really needed)
   */
  // protected TypeVarSymbol typeVarSymbol;

  public String getVarName() {
    return varName;
  }
  
  public void setVarName(String name) {
    this.varName = name;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getVarName();
  }


  // --------------------------------------------------------------------------


  @Override @Deprecated
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof TypeVariable)){
      return false;
    }
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(typeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public TypeExpression deepClone() {
    TypeVariable clone = new TypeVariable();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    return clone;
  }
}
