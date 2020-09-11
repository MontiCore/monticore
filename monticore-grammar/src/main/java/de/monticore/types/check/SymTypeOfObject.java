/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

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
  public String print() {
    return getObjName();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeOfObject");
    jp.member("objName", getObjName());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfObject deepClone() {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(this.typeSymbol.getName());
    typeSymbol.setEnclosingScope(this.typeSymbol.getEnclosingScope());
    return  new SymTypeOfObject(typeSymbol);
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
