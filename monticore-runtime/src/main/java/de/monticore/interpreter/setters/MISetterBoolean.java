// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValue;

/**
 * Optimized {@link MISetter} that sets booleans without boxing.
 */
@FunctionalInterface
public interface MISetterBoolean extends MISetter {

  void set(MIFrame currentFrame, boolean value);

  @Override
  default boolean isSetterBoolean() {
    return true;
  }

  @Override
  default MISetterBoolean asSetterBoolean() {
    return this;
  }

  @Override
  default void set(MIFrame frame, MCValue value) {
    set(frame, value.asBoolean());
  }

}

