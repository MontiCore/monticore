/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

public class ETypeSymbol extends ETypeSymbolTOP {

  private List<EMethodSymbol> methodSymbols;

  private List<EVariableSymbol> variableSymbols;

  public ETypeSymbol(String name) {
    super(name);
  }

  public ASTMCType getType() {
    throw new UnsupportedOperationException();
  }

  public List<EMethodSymbol> getMethodSymbols() {
    return methodSymbols;
  }

  public List<EVariableSymbol> getVariableSymbols() {
    return variableSymbols;
  }
}
