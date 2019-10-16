/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

import java.util.List;

public class SymTypeVariable extends SymTypeExpression {

  /**
   * A typeVariable has a name
   */
  protected String varName;

  /**
   * The Variable is connected to a symbol carrying that variable
   * (TODO: clarify if that is really needed)
   */
  protected TypeVarSymbol typeVarSymbol;

  public SymTypeVariable(String varName) {
    this.varName = varName;
  }

  public SymTypeVariable(String varName, TypeVarSymbol typeVarSymbol) {
    this.varName = varName;
    this.typeVarSymbol = typeVarSymbol;
  }

  public TypeVarSymbol getTypeVarSymbol() {
    return typeVarSymbol;
  }

  public void setTypeVarSymbol(TypeVarSymbol typeVarSymbol) {
    this.typeVarSymbol = typeVarSymbol;
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

  public boolean isTypeVariable() {
    return true;
  }

  @Override
  public SymTypeVariable deepClone() {
    SymTypeVariable clone = new SymTypeVariable();
    clone.setVarName(this.getVarName());
    clone.setName(this.getName());
    clone.setTypeInfo(this.getTypeInfo());
    return clone;
  }


  // --------------------------------------------------------------------------

  public SymTypeVariable() {
  }

}
