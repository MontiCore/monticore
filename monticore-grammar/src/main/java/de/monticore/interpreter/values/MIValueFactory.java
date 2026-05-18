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
      case Boolean b -> new BooleanMIValue(b);
      case Byte b -> new IntMIValue(b);
      case Character c -> new IntMIValue(c);
      case Short s -> new IntMIValue(s);
      case Integer i -> new IntMIValue(i);
      // this is as questionable as it seems;
      // currently, the default is set to int for integral types,
      // and usage of long is not advised
      // and should be checked against by a CoCo.
      // However, if required, the default can be set to long instead.
      // As of writing, this is not done, as some Java/JVM-functionality
      // is defined on ints, and as such,
      // we would like to avoid the additional complexity.
      case Long l -> new IntMIValue((int) (long) l);
      case Float f -> new DoubleMIValue(f);
      case Double d -> new DoubleMIValue(d);
      default -> new MIValueObject(value);
    };
  }

}
