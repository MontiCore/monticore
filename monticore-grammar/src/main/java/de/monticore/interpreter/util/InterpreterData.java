// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.frames.MIFrameLayout;
import de.monticore.interpreter.setters.MISetter;

import java.util.Stack;

/**
 * Contains the shared data between interpreter visitors.
 * Depending on the Interpreter, this may need to be extended.
 */
public class InterpreterData {

  // null if not present
  protected MICalculation calculation;

  // null if not present
  protected MISetter setter;

  protected Stack<MIFrameLayout> frameLayoutStack = new Stack<>();

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
    Preconditions.checkNotNull(calculation);
    MICalculation calculation = this.calculation;
    this.calculation = null;
    return calculation;
  }

  public void putCalculation(MICalculation calculation) {
    Preconditions.checkState(this.calculation == null);
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

  /**
   * The current {@link MIFrameLayout}s.
   *
   * @return the current frame layout.
   */
  public Stack<MIFrameLayout> getFrameLayoutStack() {
    return frameLayoutStack;
  }

  public void reset() {
    calculation = null;
    setter = null;
    frameLayoutStack.clear();
  }

}
