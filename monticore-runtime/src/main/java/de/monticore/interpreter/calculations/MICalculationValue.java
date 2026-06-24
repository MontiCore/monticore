// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValue;

/**
 * Generic {@link MICalculation} that returns values.
 */
@FunctionalInterface
public interface MICalculationValue extends MICalculation {

  MCValue calculate(MIFrame currentFrame);

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
