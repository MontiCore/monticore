package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValueBoolean;

/**
 * Optimized {@link MICalculation} that returns booleans without boxing.
 */
@FunctionalInterface
public interface MICalculationBoolean extends MICalculation {

  boolean calculate(MIFrame currentFrame);

  @Override
  default boolean isCalculationBoolean() {
    return true;
  }

  @Override
  default MICalculationBoolean asCalculationBoolean() {
    return this;
  }

  @Override
  default MICalculationValue asCalculationValue() {
    return frame -> new MIValueBoolean(this.calculate(frame));
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this::calculate;
  }

}
