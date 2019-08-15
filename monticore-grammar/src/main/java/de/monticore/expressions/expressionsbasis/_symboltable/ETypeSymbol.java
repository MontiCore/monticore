/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.typescalculator.TypeExpression;

import java.util.ArrayList;
import java.util.List;

public class ETypeSymbol extends ETypeSymbolTOP {

  protected List<EMethodSymbol> methodSymbols = new ArrayList<>();

  protected List<EVariableSymbol> variableSymbols = new ArrayList<>();

  protected List<TypeExpression> genericParameters = new ArrayList<>();

  protected List<TypeExpression> subTypes = new ArrayList<>();

  protected List<TypeExpression> superTypes = new ArrayList<>();

  public ETypeSymbol(String name) {
    super(name);
  }

  public List<EMethodSymbol> getMethodSymbols() {
    return methodSymbols;
  }

  public List<EVariableSymbol> getVariableSymbols() {
    return variableSymbols;
  }

  public List<TypeExpression> getGenericParameters(){
    return genericParameters;
  }

  public List<TypeExpression> getSubTypes(){
    return subTypes;
  }

  public List<TypeExpression> getSuperTypes(){
    return superTypes;
  }

  public void setMethodSymbols(List<EMethodSymbol> methodSymbols){
    this.methodSymbols=methodSymbols;
  }

  public void setGenericParameters(List<TypeExpression> genericParameters) {
    this.genericParameters = genericParameters;
  }

  public void setSuperTypes(List<TypeExpression> superTypes) {
    this.superTypes = superTypes;
  }

  public void setSubTypes(List<TypeExpression> subTypes) {
    this.subTypes = subTypes;
  }

  public void setVariableSymbols(List<EVariableSymbol> variableSymbols) {
    this.variableSymbols = variableSymbols;
  }

  public boolean deepEquals(ETypeSymbol symbol){
    if(symbol==null){
      return false;
    }
    if(methodSymbols!=null&&symbol.methodSymbols!=null&&!this.methodSymbols.equals(symbol.methodSymbols)){
      return false;
    }
    if(variableSymbols!=null&&symbol.variableSymbols!=null&&!this.variableSymbols.equals(symbol.variableSymbols)){
      return false;
    }
    if(superTypes!=null&&symbol.superTypes!=null&&!this.superTypes.equals(symbol.superTypes)){
      return false;
    }
    if(subTypes!=null&&symbol.subTypes!=null&&!this.subTypes.equals(symbol.subTypes)){
      return false;
    }
    if(genericParameters!=null&&symbol.genericParameters!=null&&!this.genericParameters.equals(symbol.genericParameters)){
      return false;
    }
    if(name!=null&&symbol.name!=null&&!this.name.equals(symbol.name)){
      return false;
    }
    if(enclosingScope!=null&&symbol.enclosingScope!=null&&!this.enclosingScope.equals(symbol.enclosingScope)){
      return false;
    }
    return true;
  }

}
