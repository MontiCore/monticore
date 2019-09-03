/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types2.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class ETypeSymbol extends ETypeSymbolTOP {
  
  /**
   * Liste der in diesem(!) Typ definierten Methoden.
   * TODO RE: sind da die reingeerbten Methoden mit dabei, oder sind die extra aus Superklassen zu holen
   */
  protected List<EMethodSymbol> methodSymbols = new ArrayList<>();
  
  /**
   * Liste der Attribute
   */
  protected List<EVariableSymbol> variableSymbols = new ArrayList<>();
  
  /**
   * Liste der TypVariablennamen (bei generischen Typkonstruktoren)
   * TODO: das sind keine Expressions sondern immer TypeVars
   */
  protected List<SymTypeExpression> genericParameters = new ArrayList<>();
  
  /**
   * Ernsthaft? Subklassen? Das interessiert hier doch nicht?
   */
  @Deprecated
  protected List<SymTypeExpression> subTypes = new ArrayList<>();
  
  /**
   * Liste der zu dieser Klasse gehörenden Supertypen
   * TODO RE: Klären 1) sind da Interfaces mit drin (wahrscheinlich ja?)
   *    2) ist die transitive Hülle angegeben (oder nur direkte Supertypen)
   */
  protected List<SymTypeExpression> superTypes = new ArrayList<>();

  
  
  public ETypeSymbol(String name) {
    super(name);
  }

  public List<EMethodSymbol> getMethodSymbols() {
    return methodSymbols;
  }

  public List<EVariableSymbol> getVariableSymbols() {
    return variableSymbols;
  }

  public List<SymTypeExpression> getGenericParameters(){
    return genericParameters;
  }

  public List<SymTypeExpression> getSubTypes(){
    return subTypes;
  }

  public List<SymTypeExpression> getSuperTypes(){
    return superTypes;
  }

  public void setMethodSymbols(List<EMethodSymbol> methodSymbols){
    this.methodSymbols=methodSymbols;
  }

  public void setGenericParameters(List<SymTypeExpression> genericParameters) {
    this.genericParameters = genericParameters;
  }

  public void setSuperTypes(List<SymTypeExpression> superTypes) {
    this.superTypes = superTypes;
  }

  public void setSubTypes(List<SymTypeExpression> subTypes) {
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
