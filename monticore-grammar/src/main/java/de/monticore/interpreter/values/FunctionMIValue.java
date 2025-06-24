package de.monticore.interpreter.values;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;

public interface FunctionMIValue extends MIValue {
  
  @Override
  public default boolean isFunction() {
    return true;
  }
  
  abstract public MIValue execute(ModelInterpreter interpreter, List<MIValue> arguments);
  
}
