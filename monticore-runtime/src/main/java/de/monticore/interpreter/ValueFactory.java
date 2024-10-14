/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.interpreter.values.*;

public class ValueFactory {

  public static Value createValue(int Value) {
    return new IntValue(Value);
  }

  public static Value createValue(double Value) {
    return new DoubleValue(Value);
  }

  public static Value createValue(float Value) {
    return new FloatValue(Value);
  }

  public static Value createValue(long Value) {
    return new LongValue(Value);
  }

  public static Value createValue(boolean Value) {
    return new BooleanValue(Value);
  }

  public static Value createValue(char Value) {
    return new CharValue(Value);
  }

  public static Value createValue(String Value) {
    return new StringValue(Value);
  }

  public static Value createValue(Object Value) {
    return new ObjectValue(Value);
  }

}
