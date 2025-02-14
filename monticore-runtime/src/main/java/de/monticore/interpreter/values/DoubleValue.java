/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;
import de.se_rwth.commons.logging.Log;

public class DoubleValue implements Value {

  protected double value;

  public DoubleValue(double value) {
    this.value = value;
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public double asDouble() {
    return value;
  }
  
}
