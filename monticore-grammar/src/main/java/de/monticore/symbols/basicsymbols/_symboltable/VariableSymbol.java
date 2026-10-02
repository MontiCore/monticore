/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

public class VariableSymbol extends VariableSymbolTOP {

  public VariableSymbol(String name){
    super(name);
  }

  public VariableSymbol deepClone(){
    VariableSymbol clone = new VariableSymbol(name);
    clone.setAccessModifier(this.accessModifier);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if(type!=null){
      clone.setType(type.deepClone());
    }
    return clone;
  }

  @Override
  public AccessModifier getAccessModifier() {
    // supporting legacy source code...
    if(accessModifier == null) {
      Log.trace("AccessModifier of variable '"
              + getFullName() + "' was not set (null)",
          "BasicSymbols");
      accessModifier = AccessModifier.ALL_INCLUSION;
    }
    return accessModifier;
  }
}
