/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.interpreter.values.BooleanMIValue;
import de.monticore.interpreter.values.ByteMIValue;
import de.monticore.interpreter.values.CharMIValue;
import de.monticore.interpreter.values.DoubleMIValue;
import de.monticore.interpreter.values.FloatMIValue;
import de.monticore.interpreter.values.IntMIValue;
import de.monticore.interpreter.values.LongMIValue;
import de.monticore.interpreter.values.ObjectMIValue;
import de.monticore.interpreter.values.ShortMIValue;

public class MIValueFactory {

  public static MIValue createValue(short value) {
    return new ShortMIValue(value);
  }

  public static MIValue createValue(int value) {
    return new IntMIValue(value);
  }

  public static MIValue createValue(double value) {
    return new DoubleMIValue(value);
  }

  public static MIValue createValue(float value) {
    return new FloatMIValue(value);
  }

  public static MIValue createValue(long value) {
    return new LongMIValue(value);
  }

  public static MIValue createValue(boolean value) {
    return new BooleanMIValue(value);
  }

  public static MIValue createValue(char value) {
    return new CharMIValue(value);
  }

  public static MIValue createValue(byte value) {
    return new ByteMIValue(value);
  }

  public static MIValue createValue(Object value) {
    return new ObjectMIValue(value);
  }

}
