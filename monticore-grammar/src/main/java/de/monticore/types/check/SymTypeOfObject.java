/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

/**
 * An objectType is a full qualified class name.
 * Therefore, we have the fullName, the baseName and the
 * Symbol behind that full qualified class name to retrieve
 */
public class SymTypeOfObject extends SymTypeExpression {

  /**
   * Constructor: with a TypeSymbolSurrogate that contains the name and enclosingScope
   */
  public SymTypeOfObject(TypeSymbol typeSymbol)
  {
    this.typeSymbol = typeSymbol;
  }

  public String getObjName() {
    return typeSymbol.getFullName();
  }
  
  public void setObjName(String objname) {
    this.typeSymbol.setName(objname);
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String printFullName() {
    return getObjName();
  }

  @Override
  public String print(){
    return typeSymbol.getName();
  }

  @Override
  public SymTypeOfObject deepClone() {
    return  new SymTypeOfObject(this.typeSymbol);
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getObjName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isObjectType() {
    return true;
  }


  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypeOfObject)){
      return false;
    }
    SymTypeOfObject symCon = (SymTypeOfObject) sym;
    if(this.typeSymbol == null ||symCon.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symCon.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symCon.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symCon.print());
  }

  // --------------------------------------------------------------------------
}
