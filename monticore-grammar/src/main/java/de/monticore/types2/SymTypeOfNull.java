package de.monticore.types2;

public class SymTypeOfNull extends SymTypeExpression {
  
  /**
   * This Class represents the type of the value "null".
   * This type doesn't really exist (hence the print method delivers "nullType"),
   * but the object is used to attach "null" a proper type,
   * which is then compatible to e.g. to TypeConstant or TypeArray,
   *       int[] j = null;        ok
   *       Integer i2 = null;     ok
   * but not e.g. to int
   *       int i = null;          illegal
   */
  public SymTypeOfNull() {
    setTypeInfo(DefsTypeBasic._null);
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
      return "nullType";
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+print()+"\"";
  }
    
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }
  
  @Override @Deprecated
  public SymTypeExpression deepClone() {
    return new SymTypeOfNull();
  }
  
}
