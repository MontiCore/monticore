/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.Optional;

/**
 * An objectType is a full qualified class name.
 * Therefore, we have the fullName, the baseName and the
 * Symbol behind that full qualified class name to retrieve
 */
public class SymTypeOfObject extends SymTypeExpression {
  
  /**
   * An SymTypeOfObject has a name.
   * This is always the full qualified name (i.e. including package)
   */
  protected String objFullName;
  
  public SymTypeOfObject(String objFullName, TypeSymbol typeInfo)
  {
    this.objFullName = objFullName;
    this.typeInfo = Optional.of(typeInfo);
  }
  
  public String getObjName() {
    return objFullName;
  }
  
  public void setObjName(String objname) {
    this.objFullName = objname;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getObjName();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    //TODO: anpassen, nachdem package umbenannt ist
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeOfObject");
    jp.member("objName", getObjName());
    jp.endObject();
    return jp.getContent();
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
  
  // --------------------------------------------------------------------------
  
  @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeOfObject)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    if(!this.typeInfo.equals(symTypeExpression.typeInfo)){
      return false;
    }

    return true;
  }

  @Deprecated
  public SymTypeOfObject(String name){
    this.objFullName = name;
  }
  

}
