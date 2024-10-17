/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class ObjectValue implements Value {

  protected Object value;

  public ObjectValue(Object value) {
    this.value = value;
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public Object asObject() {
    return value;
  }
}
