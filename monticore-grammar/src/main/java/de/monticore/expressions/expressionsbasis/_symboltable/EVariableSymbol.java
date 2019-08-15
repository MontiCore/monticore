/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.typescalculator.TypeExpression;

public class EVariableSymbol extends EVariableSymbolTOP {

  protected TypeExpression type;

  public EVariableSymbol(String name) {
    super(name);
  }

  public TypeExpression getType(){
    return type;
  }

  public void setType(TypeExpression type){
    this.type=type;
  }

}
