/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types2.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

/*
    Symbol Facade to be adapted by aggregated languages
 */
@Deprecated
public class EMethodSymbol extends EMethodSymbolTOP {
  
  /**
   * Liste der Argumente mit deren Typen
   * TODO: Umgang mit "..." Parameter
   */
  protected List<EVariableSymbol> parameters = new ArrayList<>();
  
  /**
   * ErgebnisTyp (incl. void)
   */
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
