/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

public class AssocRoleSymbol extends AssocRoleSymbolTOP {
  
  public AssocRoleSymbol(String name) {
    super(name);
  }
  
  public AssocRoleSymbol getOtherSide() {
    return getAssoc().getOtherRole(this);
  }
}
