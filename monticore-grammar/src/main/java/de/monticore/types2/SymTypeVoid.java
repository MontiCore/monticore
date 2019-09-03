package de.monticore.types2;

public class SymTypeVoid extends SymTypeExpression {
    
    public SymTypeVoid() {
    }
    
    /**
     * print: Umwandlung in einen kompakten String
     */
    public String print() {
      return "void";
    }

    
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }
  
  @Override @Deprecated
  public SymTypeExpression deepClone() {
    return new SymTypeVoid();
  }
  
}
