// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

public interface MICalculation {

  default boolean isCalculationBoolean() {
    return false;
  }

  default boolean isCalculationInt() {
    return false;
  }

  default boolean isCalculationDouble() {
    return false;
  }

  default boolean isCalculationValue() {
    return false;
  }

  default boolean isCalculationVoid() {
    return false;
  }

  default MICalculationBoolean asCalculationBoolean() {
    throw new RuntimeException(
        "Not a " + MICalculationBoolean.class.getSimpleName()
    );
  }

  default MICalculationInt asCalculationInt() {
    throw new RuntimeException(
        "Not a " + MICalculationInt.class.getSimpleName()
    );
  }

  default MICalculationDouble asCalculationDouble() {
    throw new RuntimeException(
        "Not a " + MICalculationDouble.class.getSimpleName()
    );
  }

  default MICalculationValue asCalculationValue() {
    throw new RuntimeException(
        "Not a " + MICalculationValue.class.getSimpleName()
    );
  }

  /**
   * Returns this as a calculation without return value.
   * Every {@link MICalculation}
   * can be converted to a {@link MICalculationVoid}.
   *
   * @return This, but without return value if there was any.
   */
  MICalculationVoid asCalculationVoid();

}
