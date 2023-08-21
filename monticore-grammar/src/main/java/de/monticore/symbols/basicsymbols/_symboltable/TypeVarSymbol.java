// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

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
    // allow null as spanned scope
    if (this.spannedScope != other.spannedScope) {
      return false;
    }
    if (!this.getName().equals(other.getName())) {
      return false;
    }
    return true;
  }

  @Override
  public AccessModifier getAccessModifier() {
    // supporting legacy source code...
    if(accessModifier == null) {
      Log.trace("AccessModifier of type variable '"
              + getFullName() + "' was not set (null)",
          "BasicSymbols");
      accessModifier = AccessModifier.ALL_INCLUSION;
    }
    return accessModifier;
  }
}
