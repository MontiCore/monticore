// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueError;
import de.monticore.values.MCValueVoid;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * (Internal) API to use the interpreter.
 * The public methods are specific
 * to the language that the interpreter is written for,
 * thus, they are given by the subclasses of this.
 */
public abstract class AbstractInterpreter {

  protected Map<ASTNode, MICalculation> calculationCache =
      new WeakHashMap<>();

  public abstract MIFrame getTopMostFrame();

  // interpretation

  protected MCValue interpretNode(ASTNode node) {
    MICalculation calculation = getCalculation(node);
    MCValue value = calculateWithCatch(calculation, getTopMostFrame());
    return value;
  }

  // evaluation

  /**
   * Converts an executable {@link ASTNode} into an {@link MICalculation}
   * that can be executed with a given frame.
   *
   * @param node the node describing the behavior
   * @return an {@link MICalculation} representing the behavior
   */
  protected MICalculation getCalculation(ASTNode node) {
    Preconditions.checkNotNull(node);
    if (!calculationCache.containsKey(node)) {
      MICalculation newCalc = getCalculationWithoutCaching(node);
      Preconditions.checkNotNull(newCalc);
      calculationCache.put(node, newCalc);
    }
    return calculationCache.get(node);
  }

  /**
   * s. {@link #getCalculation(ASTNode)}.
   *
   * @param node
   * @return
   */
  abstract protected MICalculation getCalculationWithoutCaching(ASTNode node);

  /**
   * Takes any {@link MICalculation} and executes it with the given frame.
   * <p>
   * Mostly to handle the {@link MICalculationVoid} case.
   *
   * @param calculation    to be executed. May or may not return a value.
   * @param enclosingFrame the enclosing frame.
   * @return The value of the calculation or {@link MCValueVoid}.
   */
  protected MCValue calculate(
      MICalculation calculation,
      MIFrame enclosingFrame
  ) {
    MICalculationValue valueCalc;
    if (calculation.isCalculationVoid()) {
      valueCalc = frame -> {
        calculation.asCalculationVoid().calculate(frame);
        return MCValueVoid.INSTANCE;
      };
    }
    else {
      valueCalc = calculation.asCalculationValue();
    }
    return valueCalc.calculate(enclosingFrame);
  }

  protected MCValue calculateWithCatch(
      MICalculation calculation,
      MIFrame enclosingFrame
  ) {
    MCValue value;
    try {
      value = calculate(calculation, enclosingFrame);
    }
    // catch everything for now,
    // there may be exceptions to this rule
    catch (Throwable e) {
      value = new MCValueError(e);
    }
    return value;
  }

}
