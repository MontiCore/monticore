// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValueDouble;

/**
 * Optimized {@link MICalculation} that returns doubles without boxing.
 */
@FunctionalInterface
public interface MICalculationDouble extends MICalculation {

  double calculate(MIFrame currentFrame);

  @Override
  default boolean isCalculationDouble() {
    return true;
  }

  @Override
  default MICalculationDouble asCalculationDouble() {
    return this;
  }

  // downcast
  @Override
  default MICalculationInt asCalculationInt() {
    return frame -> (int) this.calculate(frame);
  }

  @Override
  default MICalculationValue asCalculationValue() {
    return frame -> new MIValueDouble(this.calculate(frame));
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this::calculate;
  }

}

