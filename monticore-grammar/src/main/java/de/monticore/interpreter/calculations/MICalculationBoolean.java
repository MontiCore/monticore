package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.BooleanMIValue;

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
    return frame -> new BooleanMIValue(this.calculate(frame));
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this::calculate;
  }

}
