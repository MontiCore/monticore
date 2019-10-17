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
  
  /**
   * Constructor: with the full name and the TypeSymbol behind
   * @param objFullName
   * @param typeInfo
   */
  public SymTypeOfObject(String objFullName, TypeSymbol typeInfo)
  {
    this.objFullName = objFullName;
    this.typeInfo = typeInfo;
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
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeOfObject");
    jp.member("objName", getObjName());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfObject deepClone() {
    SymTypeOfObject clone = new SymTypeOfObject(this.objFullName,this.getTypeInfo());
    return clone;
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

  // TODO 4: this constructor ignores the typeInfo:
  @Deprecated
  public SymTypeOfObject(String name){
    this.objFullName = name;
  }
  

}
