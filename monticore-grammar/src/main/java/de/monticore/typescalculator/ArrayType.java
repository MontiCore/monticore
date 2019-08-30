package de.monticore.typescalculator;

public class ArrayType extends TypeExpression {
  
  /**
   * An arrayType has a dimension
   */
  protected int dim;
  
  /**
   * An Array has an argument Type
   */
  protected TypeExpression argument;
  
  public int getDim() {
    return dim;
  }
  
  public void setDim(int dim) {
    this.dim = dim;
  }
  
  public TypeExpression getArgument() {
    return argument;
  }
  
  public void setArgument(TypeExpression argument) {
    this.argument = argument;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getArgument().print() + "[]XXX";
  }
  
  
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(TypeExpression typeExpression) {
    return false;
  }
  
  @Override @Deprecated // and not implemented yet
  public TypeExpression deepClone() {
    return null;
  }
  
}
