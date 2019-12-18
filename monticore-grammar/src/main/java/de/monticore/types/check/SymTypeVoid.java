package de.monticore.types.check;


public class SymTypeVoid extends SymTypeExpression {
  
  public SymTypeVoid() {
//    typeSymbolLoader = new TypeSymbolLoader(DefsTypeBasic._voidTypeString,
//        BuiltInJavaTypeSymbolResolvingDelegate.getScope());
    typeSymbolLoader = new PseudoTypeSymbolLoader(DefsTypeBasic._void);
  }
  
  /**
     * print: Umwandlung in einen kompakten String
     */
  @Override
  public String print() {
    return "void";
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+DefsTypeBasic._voidTypeString+"\"";
  }

  @Override
  public SymTypeVoid deepClone() {
    return new SymTypeVoid();
  }

  
}
