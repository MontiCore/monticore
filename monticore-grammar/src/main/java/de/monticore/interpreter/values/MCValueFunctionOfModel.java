package de.monticore.interpreter.values;

import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.frames.MIFrameLayout;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFunction;

import java.util.List;

// Note: no var-args support yet,
// but, we currently also have no language with support for it either.

/**
 * Used to represent a function declared inside a model,
 * e.g., a lambda by means of an {@link MICalculation}.
 */
public class MCValueFunctionOfModel implements MCValueFunction {

  protected MIFrame parentFrame;
  protected final MIFrameLayout frameLayout;
  protected final MISetter[] parameterSetters;
  protected final MICalculationValue calculation;

  public MCValueFunctionOfModel(
      MIFrame parentFrame,
      MIFrameLayout frameLayout,
      List<MISetter> parameterSetters,
      MICalculation calculation
  ) {
    this(
        parentFrame,
        frameLayout,
        parameterSetters.toArray(new MISetter[0]),
        calculation
    );
  }

  public MCValueFunctionOfModel(
      MIFrame parentFrame,
      MIFrameLayout frameLayout,
      MISetter[] parameterSetters,
      MICalculation calculation
  ) {
    this.parentFrame = parentFrame;
    this.frameLayout = frameLayout;
    this.parameterSetters = parameterSetters;
    this.calculation = calculation.asCalculationValue();
  }

  /**
   * Evaluates the function.
   * <p>
   * Note: could be switched to an Array to be faster,
   * but it is untested if this is relevant.
   *
   * @param arguments the arguments passed to the function.
   *                  for non-static methods, the first arguments is this.
   * @return the calculated value.
   */
  @Override
  public MCValue execute(
      MCValue[] arguments
  ) {
    MIFrame newFrame = new MIFrame(frameLayout, parentFrame);
    for (int i = 0; i < arguments.length; i++) {
      parameterSetters[i].set(newFrame, arguments[i]);
    }
    return calculation.asCalculationValue().calculate(newFrame);
  }

  @Override
  public String printType() {
    return "Model-Function";
  }

  // helper

  /**
   * Internal functionality for, e.g., a REPL;
   * you usually don't need this!
   * <p>
   * However, if the parent frame is expanded with further variables
   * after creation of the function (e.g., the topmost frame in a REPL),
   * then this can be used to exchange the parent frame accordingly.
   * <p>
   * This should not be called within the interpreter visitors themselves.
   *
   * @param newParentFrame the new parent frame
   */
  public void setNewParentFrame(MIFrame newParentFrame) {
    parentFrame = newParentFrame;
  }

}
