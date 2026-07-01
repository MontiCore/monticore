// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.setters.MISetter;

/**
 * Contains the shared data between interpreter visitors.
 * Depending on the Interpreter, this may need to be extended.
 */
public class InterpreterData {

  static protected final String NOT_FULLY_IMPLEMENTED_MESSAGE =
      "This Exception implies that a case in the interpreter" +
          " has not been implemented"
          + " (or the corresponding visitor is not being used)."
          + System.lineSeparator()
          + "A calculation is being expected but was not set."
          + System.lineSeparator()
          + "Please check the interpreter's setup/documentation.";

  // null if not present
  protected MICalculation calculation;

  // null if not present
  protected MISetter setter;

  public boolean isPresentCalculation() {
    return calculation != null;
  }

  /**
   * If a node is traversed,
   * this contains the {@link MICalculation} to interpret it.
   * <p>
   * SideEffect: This will clear the calculation,
   * so only call this once.
   *
   * @return The current calculation.
   */
  public MICalculation popCalculation() {
    Preconditions.checkNotNull(calculation, NOT_FULLY_IMPLEMENTED_MESSAGE);
    MICalculation calculation = this.calculation;
    this.calculation = null;
    return calculation;
  }

  public void putCalculation(MICalculation calculation) {
    Preconditions.checkState(
        this.calculation == null,
        NOT_FULLY_IMPLEMENTED_MESSAGE
    );
    this.calculation = Preconditions.checkNotNull(calculation);
  }

  /**
   * If a node is an LValue,
   * this contains the {@link MISetter} to set the new value.
   * <p>
   * SideEffect: This will clear the setter,
   * so only call this once.
   *
   * @return The current setter.
   */
  public MISetter popSetter() {
    Preconditions.checkState(setter != null,
        "Expected a setter at this point, but non has been created."
            + " This is an internal tooling error;"
            + " likely, either CoCos are missing,"
            + " or the interpreter's visitors are not configured properly"
    );
    MISetter setter = this.setter;
    this.setter = null;
    return setter;
  }

  /**
   * Sets the current {@link MISetter},
   * used while traversing LValues.
   *
   * @param setter the current
   */
  public void putSetter(MISetter setter) {
    Preconditions.checkNotNull(setter);
    this.setter = setter;
  }

  public void reset() {
    calculation = null;
    setter = null;
  }

}
