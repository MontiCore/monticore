/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

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
    if(!this.typeSymbol.equals(symTypeExpression.typeSymbol)){
      return false;
    }
    // TODO RE: supertypen muss man doch nicht klonen?
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(symTypeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeOfObject clone = new SymTypeOfObject(objFullName,null);
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);

    for(SymTypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    return clone;
  }
  
  @Deprecated
  public SymTypeOfObject() {
  }
  
  
}
