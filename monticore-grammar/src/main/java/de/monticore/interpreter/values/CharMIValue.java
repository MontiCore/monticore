/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class CharMIValue implements MIValue {

  protected char value;

  public CharMIValue(char value){
    this.value = value;
  }
  
  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean isChar() {
    return true;
  }

  @Override
  public int asInt() {
    return value;
  }

  @Override
  public double asDouble() {
    return value;
  }

  @Override
  public char asChar() {
    return value;
  }

  @Override
  public long asLong() {
    return value;
  }

  @Override
  public float asFloat() {
    return value;
  }
  
  @Override
  public String printType() {
    return "Char";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }

}
