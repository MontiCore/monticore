package de.monticore.interpreter.values;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;

import java.util.List;

abstract public class FunctionMIValue implements MIValue {
  
  protected MIScope parentScope;
  
  public FunctionMIValue(MIScope parentScope) {
    this.parentScope = parentScope;
  }
  
  @Override
  public boolean isFunction() {
    return true;
  }
  
  /*
  TODO MIFunctionSymbol, MIMethodSymbol, MILambdaValue
   */
  
  abstract public MIValue execute(List<MIValue> arguments);
  
  
}
