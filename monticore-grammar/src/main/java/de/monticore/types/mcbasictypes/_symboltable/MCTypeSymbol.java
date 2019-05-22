package de.monticore.types.mcbasictypes._symboltable;

import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

public class MCTypeSymbol extends MCTypeSymbolTOP  {
  private List<MCTypeSymbol> supertypes;
  private List<MCTypeSymbol> subtypes;
  private ASTMCType type;
  private EVariableSymbol symbol;
  private EMethodSymbol methodSymbol;

  public MCTypeSymbol(String name){
    super(name);
  }

  public void setEVariableSymbol(EVariableSymbol symbol){
    this.symbol=symbol;
  }

  public EVariableSymbol getEVariableSymbol(){
    return symbol;
  }

  public void setASTMCType(ASTMCType type) {
    this.type = type;
  }

  public void setSubtypes(List<MCTypeSymbol> subtypes) {
    this.subtypes = subtypes;
  }

  public void setSupertypes(List<MCTypeSymbol> supertypes) {
    this.supertypes = supertypes;
  }

  public ASTMCType getASTMCType() {
    return type;
  }

  public List<MCTypeSymbol> getSubtypes() {
    return subtypes;
  }

  public List<MCTypeSymbol> getSupertypes() {
    return supertypes;
  }

  public boolean deepEquals(MCTypeSymbol symbol){
    return this.type.deepEquals(symbol.getASTMCType());
  }

  public boolean deepEqualsWithType(ASTMCType type){
    return this.type.deepEquals(type);
  }

  public EMethodSymbol getMethodSymbol(){
    return methodSymbol;
  }

  public void setMethodSymbol(EMethodSymbol methodSymbol){
    this.methodSymbol=methodSymbol;
  }

}
