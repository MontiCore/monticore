/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.typescalculator.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class ETypeSymbolReference extends ETypeSymbolReferenceTOP {

  protected List<SymTypeExpression> genericArguments = new ArrayList<>();

  public ETypeSymbolReference(String name, IExpressionsBasisScope enclosingScopeOfReference){
    super(name,enclosingScopeOfReference);
  }

  public List<SymTypeExpression> getGenericArguments() {
    return genericArguments;
  }

  public void setGenericArguments(List<SymTypeExpression> genericArguments) {
    this.genericArguments = genericArguments;
  }

  public ETypeSymbolReference deepClone(){
    ETypeSymbolReference clone = ExpressionsBasisSymTabMill.eTypeSymbolReferenceBuilder().setName(this.name).setEnclosingScopeOfReference(this.enclosingScope).build();
    clone.setFullName(getFullName());
    ArrayList<SymTypeExpression> supTypes = new ArrayList<>();
    supTypes.addAll(getSuperTypes());
    clone.setSuperTypes(supTypes);
    ArrayList<SymTypeExpression> subTypes = new ArrayList<>();
    subTypes.addAll(getSubTypes());
    clone.setSubTypes(subTypes);
    ArrayList<SymTypeExpression> genArgs = new ArrayList<>();
    genArgs.addAll(getGenericArguments());
    clone.setGenericArguments(genArgs);
    ArrayList<EMethodSymbol> methodSym = new ArrayList<>();
    methodSym.addAll(getMethodSymbols());
    clone.setMethodSymbols(methodSym);
    ArrayList<EVariableSymbol> variableSym = new ArrayList<>();
    variableSym.addAll(getVariableSymbols());
    clone.setVariableSymbols(variableSym);
    return clone;
  }
}
