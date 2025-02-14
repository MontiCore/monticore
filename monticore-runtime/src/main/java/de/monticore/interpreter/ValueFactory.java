/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.interpreter.values.*;

public class ValueFactory {
  
  public static Value createValue(short value) {
    return new ShortValue(value);
  }

  public static Value createValue(int value) {
    return new IntValue(value);
  }

  public static Value createValue(double value) {
    return new DoubleValue(value);
  }

  public static Value createValue(float value) {
    return new FloatValue(value);
  }

  public static Value createValue(long value) {
    return new LongValue(value);
  }

  public static Value createValue(boolean value) {
    return new BooleanValue(value);
  }

  public static Value createValue(char value) {
    return new CharValue(value);
  }
  
  public static Value createValue(byte value) {
    return new ByteValue(value);
  }

  public static Value createValue(Object value) {
    return new ObjectValue(value);
  }

}
