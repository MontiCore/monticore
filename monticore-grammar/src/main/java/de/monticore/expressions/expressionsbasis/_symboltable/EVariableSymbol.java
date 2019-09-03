/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types2.SymTypeExpression;

public class EVariableSymbol extends EVariableSymbolTOP {

  protected SymTypeExpression type;

  public EVariableSymbol(String name) {
    super(name);
  }

  public SymTypeExpression getType(){
    return type;
  }

  public void setType(SymTypeExpression type){
    this.type=type;
  }

}
