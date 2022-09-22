/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

public class SymTypeObscure extends SymTypeExpression {

  @Override
  public String print() {
    return "Obscure";
  }

  @Override
  public String printFullName() {
    return "Obscure";
  }

  @Override
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeObscure");
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeExpression deepClone() {
    return new SymTypeObscure();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    return sym.isObscureType();
  }

  @Override
  public boolean isObscureType() {
    return true;
  }
}
