// (c) https://github.com/MontiCore/monticore
package de.monticore.values;

/**
 * Factory to create MIValues.
 * Mostly constructors are used directly in the current version
 * for efficiency reasons
 */
public class MCValueFactory {

  public static MCValue createMIValueOfNativeObject(Object value) {
    return switch (value) {
      case MCValue MCValue -> MCValue;
      case Boolean b -> new MCValueBoolean(b);
      case Byte b -> new MCValueInt(b);
      case Character c -> new MCValueInt(c);
      case Short s -> new MCValueInt(s);
      case Integer i -> new MCValueInt(i);
      // this is as questionable as it seems;
      // currently, the default is set to int for integral types,
      // and usage of long is not advised
      // and should be checked against by a CoCo.
      // However, if required, the default can be set to long instead.
      // As of writing, this is not done, as some Java/JVM-functionality
      // is defined on ints, and as such,
      // we would like to avoid the additional complexity.
      case Long l -> new MCValueInt((int) (long) l);
      case Float f -> new MCValueDouble(f);
      case Double d -> new MCValueDouble(d);
      default -> new MCValueObject(value);
    };
  }

}
