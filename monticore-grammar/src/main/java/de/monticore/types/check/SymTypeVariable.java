/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;

public class SymTypeVariable extends SymTypeExpression {

  /**
   * A typeVariable has a name
   */
  protected String varName;
  
  /**
   * The Variable is connected to a symbol carrying that variable
   * (TODO: clarify if that is really needed)
   */
  // TODO protected TypeVarSymbol typeVarSymbol;
  
  public SymTypeVariable(String varName)
  {
    this.varName = varName;
  }


  public String getVarName() {
    return varName;
  }
  
  public void setVarName(String name) {
    this.varName = name;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getVarName();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    //TODO: anpassen, nachdem package umbenannt ist
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeVariable");
    jp.member("varName", getVarName());
    jp.endObject();
    return jp.getContent();
  }
  
  /**
   * Am I primitive? (such as "int")
   */
  public boolean isPrimitiveType() {
    return false;
    // TODO: ?sometimes the var is, sometimes not ...
    // Unless we always assume boxed implementations then return false would be correct
  }
  

  // --------------------------------------------------------------------------


  @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeVariable)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }

    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeVariable clone = new SymTypeVariable();
    clone.setName(this.name);
    
    return clone;
  }
  
  public SymTypeVariable() {
  }
  
}
