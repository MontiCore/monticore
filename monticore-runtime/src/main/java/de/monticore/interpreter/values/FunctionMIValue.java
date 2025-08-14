package de.monticore.interpreter.values;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;

import java.util.List;

@FunctionalInterface
public interface FunctionMIValue extends MIValue {

  @Override
  default boolean isFunction() {
    return true;
  }

  @Override
  default FunctionMIValue asFunction() {
    return this;
  }

  MIValue execute(IModelInterpreter interpreter, List<MIValue> arguments);

}
