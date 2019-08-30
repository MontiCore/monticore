package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class TypeVoid extends TypeExpression {
    
    public TypeVoid() {
    }
    
    /**
     * print: Umwandlung in einen kompakten String
     */
    public String print() {
      return "void";
    }

    
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(TypeExpression typeExpression) {
    return false;
  }
  
  @Override @Deprecated
  public TypeExpression deepClone() {
    return new TypeVoid();
  }
  
}
