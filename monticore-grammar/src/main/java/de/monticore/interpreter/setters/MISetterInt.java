// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValue;

@FunctionalInterface
public interface MISetterInt extends MISetter {

  void set(MIFrame currentFrame, int value);

  @Override
  default boolean isSetterInt() {
    return true;
  }

  @Override
  default MISetterInt asSetterInt() {
    return this;
  }

  @Override
  default MISetterDouble asSetterDouble() {
    return (frame, value) -> set(frame, (int) value);
  }

  @Override
  default void set(MIFrame frame, MIValue value) {
    set(frame, value.asInt());
  }

}
