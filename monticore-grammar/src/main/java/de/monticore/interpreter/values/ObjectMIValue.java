/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class ObjectMIValue implements MIValue {

  protected Object value;

  public ObjectMIValue(Object value) {
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
  
  @Override
  public String printType() {
    String typeStr = value != null
        ? value.getClass().getTypeName()
        : "null";
    return "Object(" + typeStr + ")";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }
}
