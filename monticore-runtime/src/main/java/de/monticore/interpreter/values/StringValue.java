/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class StringValue implements Value {

  protected String value;

  public StringValue(String value) {
    this.value = value;
  }

  @Override
  public boolean isString() {
    return true;
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public String asString() {
    return value;
  }

  @Override
  public Object asObject() {
    return value;
  }
}
