package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class VoidValue implements Value {
  
  @Override
  public boolean isVoid() {
    return true;
  }
}
