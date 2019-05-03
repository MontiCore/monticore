package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

/*
    Symbol Facade to be adapted by aggregated languages
 */
public class EMethodSymbol extends EMethodSymbolTOP {

  private ASTMCReturnType returnType;
  private MCTypeSymbol symbol;

  public EMethodSymbol(String name) {
    super(name);
  }

  public void setReturnType(ASTMCReturnType returnType){
    this.returnType=returnType;
  }

  public void setMCTypeSymbol(MCTypeSymbol symbol){
    this.symbol = symbol;
  }

  public MCTypeSymbol getMCTypeSymbol(){
    return symbol;
  }

  public ASTMCReturnType getReturnType() {
    return returnType;
  }

}
