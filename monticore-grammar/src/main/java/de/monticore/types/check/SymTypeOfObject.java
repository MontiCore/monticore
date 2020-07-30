/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
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
  public SymTypeOfObject(OOTypeSymbolSurrogate typeSymbolSurrogate)
  {
    this.typeSymbolSurrogate = typeSymbolSurrogate;
  }

  public String getObjName() {
    return typeSymbolSurrogate.getName();
  }
  
  public void setObjName(String objname) {
    this.typeSymbolSurrogate.setName(objname);
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
    OOTypeSymbolSurrogate ooTypeSymbolSurrogate = new OOTypeSymbolSurrogate(typeSymbolSurrogate.getName());
    ooTypeSymbolSurrogate.setEnclosingScope(typeSymbolSurrogate.getEnclosingScope());
    return  new SymTypeOfObject(ooTypeSymbolSurrogate);
  }

  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getObjName();
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
    if(this.typeSymbolSurrogate== null ||symCon.typeSymbolSurrogate==null){
      return false;
    }
    if(!this.typeSymbolSurrogate.getEnclosingScope().equals(symCon.typeSymbolSurrogate.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbolSurrogate.getName().equals(symCon.typeSymbolSurrogate.getName())){
      return false;
    }
    return this.print().equals(symCon.print());
  }

  // --------------------------------------------------------------------------
}
