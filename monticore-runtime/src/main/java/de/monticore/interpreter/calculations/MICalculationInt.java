// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValueInt;

/**
 * Optimized {@link MICalculation} that returns ints without boxing.
 */
@FunctionalInterface
public interface MICalculationInt extends MICalculation {

  int calculate(MIFrame currentFrame);

  @Override
  default boolean isCalculationInt() {
    return true;
  }

  @Override
  default MICalculationInt asCalculationInt() {
    return this;
  }

  @Override
  default MICalculationDouble asCalculationDouble() {
    return this::calculate;
  }

  @Override
  default MICalculationValue asCalculationValue() {
    return frame -> new MCValueInt(calculate(frame));
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this::calculate;
  }
}
