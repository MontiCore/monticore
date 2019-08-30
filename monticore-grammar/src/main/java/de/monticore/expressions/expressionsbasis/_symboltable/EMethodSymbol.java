/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.typescalculator.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

/*
    Symbol Facade to be adapted by aggregated languages
 */
public class EMethodSymbol extends EMethodSymbolTOP {

  protected List<EVariableSymbol> parameters = new ArrayList<>();

  protected SymTypeExpression returnType;

  public EMethodSymbol(String name) {
    super(name);
  }

  public List<EVariableSymbol> getParameterList(){
    return parameters;
  }

  public SymTypeExpression getReturnType(){
    return returnType;
  }

  public void setReturnType(SymTypeExpression returnType){
    this.returnType=returnType;
  }

  public void setParameterList(List<EVariableSymbol> parameters){
    this.parameters=parameters;
  }

  public void addParameter(EVariableSymbol parameter){
    this.parameters.add(parameter);
  }

}
