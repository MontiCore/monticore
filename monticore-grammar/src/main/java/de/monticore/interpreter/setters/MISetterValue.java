// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValue;

@FunctionalInterface
public interface MISetterValue extends MISetter {

  void set(MIFrame currentFrame, MIValue value);

  @Override
  default boolean isSetterValue() {
    return true;
  }

  @Override
  default MISetterValue asSetterValue() {
    return this;
  }

}
