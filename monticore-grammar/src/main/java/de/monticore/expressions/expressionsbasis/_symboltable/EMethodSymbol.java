/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.ArrayList;
import java.util.List;

/*
    Symbol Facade to be adapted by aggregated languages
 */
public class EMethodSymbol extends EMethodSymbolTOP {

  private ASTMCReturnType returnType;
  private MCTypeSymbol symbol;
  private List<MCTypeSymbol> arguments;
  private List<EVariableSymbol> eVariableSymbols;

  public EMethodSymbol(String name) {
    super(name);
    arguments=new ArrayList<>();
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

  public List<MCTypeSymbol> getArguments(){
    return arguments;
  }

  public void setArguments(List<MCTypeSymbol> arguments) {
    this.arguments = arguments;
  }

  public List<EVariableSymbol> geteVariableSymbols() {
    return eVariableSymbols;
  }

  public void seteVariableSymbols(List<EVariableSymbol> eVariableSymbols) {
    this.eVariableSymbols = eVariableSymbols;
  }
}
