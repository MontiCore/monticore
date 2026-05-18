// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValue;

@FunctionalInterface
public interface MICalculationValue extends MICalculation {

  MIValue calculate(MIFrame currentFrame);

  @Override
  default boolean isCalculationValue() {
    return true;
  }

  @Override
  default MICalculationValue asCalculationValue() {
    return this;
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this::calculate;
  }

}
