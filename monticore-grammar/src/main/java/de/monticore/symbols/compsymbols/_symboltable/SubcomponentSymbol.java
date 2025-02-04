/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

public class SubcomponentSymbol extends SubcomponentSymbolTOP {

  public SubcomponentSymbol(String name) {
    super(name);
  }

  public boolean isTypePresent() {
    return this.type != null;
  }
}
