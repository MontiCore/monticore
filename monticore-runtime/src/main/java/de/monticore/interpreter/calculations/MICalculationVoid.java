/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.calculations;

import de.monticore.interpreter.frames.MIFrame;

/**
 * {@link MICalculation} that does not return a value.
 * This is usually used to represent side effects (e.g., statements).
 */
@FunctionalInterface
public interface MICalculationVoid extends MICalculation {

  MICalculationVoid NOOP_CALC = frame -> {
  };

  void calculate(MIFrame currentFrame);

  @Override
  default boolean isCalculationVoid() {
    return true;
  }

  @Override
  default MICalculationVoid asCalculationVoid() {
    return this;
  }

  default MICalculationVoid getChainedBefore(MICalculationVoid nextCalc) {
    return frame -> {
      this.calculate(frame);
      nextCalc.calculate(frame);
    };
  }

  default MICalculation getChainedBefore(MICalculation nextCalc) {
    MICalculation chainedCalc;
    if (nextCalc.isCalculationBoolean()) {
      MICalculationBoolean booleanCalc = nextCalc.asCalculationBoolean();
      chainedCalc = (MICalculationBoolean) f -> {
        this.calculate(f);
        return booleanCalc.calculate(f);
      };
    }
    else if (nextCalc.isCalculationInt()) {
      MICalculationInt intCalc = nextCalc.asCalculationInt();
      chainedCalc = (MICalculationInt) f -> {
        this.calculate(f);
        return intCalc.calculate(f);
      };
    }
    else if (nextCalc.isCalculationDouble()) {
      MICalculationDouble doubleCalc = nextCalc.asCalculationDouble();
      chainedCalc = (MICalculationDouble) f -> {
        this.calculate(f);
        return doubleCalc.calculate(f);
      };
    }
    else if (nextCalc.isCalculationValue()) {
      MICalculationValue valueCalc = nextCalc.asCalculationValue();
      chainedCalc = (MICalculationValue) f -> {
        this.calculate(f);
        return valueCalc.calculate(f);
      };
    }
    else {
      chainedCalc = this.getChainedBefore(nextCalc.asCalculationVoid());
    }
    return chainedCalc;
  }

}
