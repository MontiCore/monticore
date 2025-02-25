package de.monticore.interpreter.values;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;

abstract public class FunctionMIValue implements MIValue {
  
  protected MIScope parentScope;
  protected List<VariableSymbol> parameterSymbols;
  
  public FunctionMIValue(MIScope parentScope, List<VariableSymbol> parameterSymbols) {
    this.parentScope = parentScope;
    this.parameterSymbols = parameterSymbols;
  }
  
  @Override
  public boolean isFunction() {
    return true;
  }
  
  abstract public MIValue execute(ModelInterpreter interpreter, List<MIValue> arguments);
  
  
}
