package de.monticore.interpreter.values;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.Value;
import de.se_rwth.commons.Symbol;

public class FunctionValue implements Value {
  
  protected MIScope parentScope;
  protected Symbol symbol;
  
  @Override
  public boolean isFunction() {
    return true;
  }
  
  
}
