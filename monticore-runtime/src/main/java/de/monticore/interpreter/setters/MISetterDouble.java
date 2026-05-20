// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValue;

/**
 * Optimized {@link MISetter} that sets doubles without boxing.
 */
@FunctionalInterface
public interface MISetterDouble extends MISetter {

  void set(MIFrame currentFrame, double value);

  @Override
  default boolean isSetterDouble() {
    return true;
  }

  @Override
  default MISetterDouble asSetterDouble() {
    return this;
  }

  @Override
  default MISetterInt asSetterInt() {
    return this::set;
  }

  @Override
  default void set(MIFrame frame, MCValue value) {
    set(frame, value.asDouble());
  }

}
