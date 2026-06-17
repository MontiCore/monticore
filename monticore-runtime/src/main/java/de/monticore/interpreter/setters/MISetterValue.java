// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.values.MCValue;

/**
 * General {@link MISetter} to set any values.
 */
@FunctionalInterface
public interface MISetterValue extends MISetter {

  void set(MIFrame currentFrame, MCValue value);

  @Override
  default boolean isSetterValue() {
    return true;
  }

  @Override
  default MISetterValue asSetterValue() {
    return this;
  }

}
