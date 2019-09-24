package de.monticore.types.check;


public class SymTypeVoid extends SymTypeExpression {
  
  public SymTypeVoid() {
    setTypeInfo(DefsTypeBasic._void);
  }
  
  /**
     * print: Umwandlung in einen kompakten String
     */
  public String print() {
    return "void";
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+print()+"\"";
  }

  @Override
  public SymTypeVoid clone() {
    return new SymTypeVoid();
  }


  // --------------------------------------------------------------------------
  
}
