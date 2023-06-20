// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols._symboltable;

public class TypeVarSymbol extends TypeVarSymbolTOP {

  public TypeVarSymbol(String name) {
    super(name);
  }

  public boolean deepEquals(TypeVarSymbol other) {
    if (this == other) {
      return true;
    }
    if (this.getEnclosingScope() != other.getEnclosingScope()) {
      return false;
    }
    if (this.getSpannedScope() != other.getSpannedScope()) {
      return false;
    }
    if (!this.getName().equals(other.getName())) {
      return false;
    }
    return true;
  }
}
