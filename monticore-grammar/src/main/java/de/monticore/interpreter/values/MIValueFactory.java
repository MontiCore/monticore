// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.values;

/**
 * Factory to create MIValues.
 * Mostly constructors are used directly in the current version
 * for efficiency reasons
 */
public class MIValueFactory {

  public static MIValue createMIValueOfNativeObject(Object value) {
    return switch (value) {
      case MIValue miValue -> miValue;
      case Boolean b -> new MIValueBoolean(b);
      case Byte b -> new MIValueInt(b);
      case Character c -> new MIValueInt(c);
      case Short s -> new MIValueInt(s);
      case Integer i -> new MIValueInt(i);
      // this is as questionable as it seems;
      // currently, the default is set to int for integral types,
      // and usage of long is not advised
      // and should be checked against by a CoCo.
      // However, if required, the default can be set to long instead.
      // As of writing, this is not done, as some Java/JVM-functionality
      // is defined on ints, and as such,
      // we would like to avoid the additional complexity.
      case Long l -> new MIValueInt((int) (long) l);
      case Float f -> new MIValueDouble(f);
      case Double d -> new MIValueDouble(d);
      default -> new MIValueObject(value);
    };
  }

}
