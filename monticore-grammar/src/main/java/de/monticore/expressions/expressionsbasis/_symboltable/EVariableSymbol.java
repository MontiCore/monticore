/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

public class EVariableSymbol extends EVariableSymbolTOP {

  private MCTypeSymbol type;

  public EVariableSymbol(String name) {
    super(name);
  }

  public MCTypeSymbol getMCTypeSymbol() {
    return type;
  }

  public void setMCTypeSymbol(MCTypeSymbol type) {
    this.type = type;
  }
}
