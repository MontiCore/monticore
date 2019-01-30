package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
/*
    Symbol Facade to be adapted by aggregated languages
 */
public class EMethodSymbol extends EMethodSymbolTOP {

  public EMethodSymbol(String name) {
    super(name);
  }


  public ASTMCType getReturnType() {
    throw new UnsupportedOperationException();
  }

}
