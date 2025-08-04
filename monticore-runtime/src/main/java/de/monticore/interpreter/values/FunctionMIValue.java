package de.monticore.interpreter.values;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;

import java.util.List;

public interface FunctionMIValue extends MIValue {
  
  @Override
  public default boolean isFunction() {
    return true;
  }
  
  @Override
  public default FunctionMIValue asFunction() {
    return this;
  }
  
  public MIValue execute(IModelInterpreter interpreter, List<MIValue> arguments);
  
}
