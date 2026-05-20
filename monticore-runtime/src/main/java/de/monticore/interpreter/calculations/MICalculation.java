// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

/**
 * Represents a calculation of the interpreter.
 * <p>
 * A calculation is the actual execution that happens during interpretation,
 * used for phase two of the interpreter.
 * Phase one is the creation of these calculations
 * to avoid traversing the AST more than once.
 * Any static decisions (e.g., which types to use) are done during phase one,
 * so these calculations are as simple as possible
 * to rely on Java's runtime optimization.
 * <p>
 * Calculations may have a return value (e.g., for expressions)
 * and may have side effects.
 */
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
