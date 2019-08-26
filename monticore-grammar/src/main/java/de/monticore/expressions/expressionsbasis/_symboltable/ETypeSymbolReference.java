/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.typescalculator.TypeExpression;

import java.util.ArrayList;
import java.util.List;

public class ETypeSymbolReference extends ETypeSymbolReferenceTOP {

  protected List<TypeExpression> genericArguments = new ArrayList<>();

  public ETypeSymbolReference(String name, IExpressionsBasisScope enclosingScopeOfReference){
    super(name,enclosingScopeOfReference);
  }

  public List<TypeExpression> getGenericArguments() {
    return genericArguments;
  }

  public void setGenericArguments(List<TypeExpression> genericArguments) {
    this.genericArguments = genericArguments;
  }

  public ETypeSymbolReference clone(){
    ETypeSymbolReference clone = ExpressionsBasisSymTabMill.eTypeSymbolReferenceBuilder().setName(this.name).setEnclosingScopeOfReference(this.enclosingScope).build();
    clone.setFullName(getFullName());
    ArrayList<TypeExpression> supTypes = new ArrayList<>();
    supTypes.addAll(getSuperTypes());
    clone.setSuperTypes(supTypes);
    ArrayList<TypeExpression> subTypes = new ArrayList<>();
    subTypes.addAll(getSubTypes());
    clone.setSubTypes(subTypes);
    ArrayList<TypeExpression> genArgs = new ArrayList<>();
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
