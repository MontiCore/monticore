package de.monticore.interpreter.values;

import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.frames.MIFrameLayout;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;

// Note: no var-args support yet,
// but, we currently also have no language with support for it either.

/**
 * Used to represent a function declared inside a model,
 * e.g., a lamda.
 */
public class ModelFunctionMIValue implements FunctionMIValue {

  protected MIFrame parentFrame;
  protected final MIFrameLayout frameLayout;
  protected final MISetter[] parameterSetters;
  protected final MICalculationValue calculation;

  public ModelFunctionMIValue(
      MIFrame parentFrame,
      MIFrameLayout frameLayout,
      List<? extends VariableSymbol> paramSymbols,
      MICalculation calculation
  ) {
    this.parentFrame = parentFrame;
    this.frameLayout = frameLayout;
    this.calculation = calculation.asCalculationValue();
    this.parameterSetters =
        paramSymbols.stream()
            .map(frameLayout::getVariableSetter)
            .toArray(MISetter[]::new);
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
  public MIValue execute(
      MIValue[] arguments
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
