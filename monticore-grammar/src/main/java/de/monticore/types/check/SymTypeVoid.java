/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;


public class SymTypeVoid extends SymTypeExpression {
  
  public SymTypeVoid() {
//    typeSymbolSurrogate = new TypeSymbolSurrogate(DefsTypeBasic._voidTypeString,
//        BuiltInJavaTypeSymbolResolvingDelegate.getScope());
    typeSymbolSurrogate = new PseudoTypeSymbolSurrogate(DefsTypeBasic._void);
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

  @Override
  public boolean isVoidType() {
    return true;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    return sym instanceof SymTypeVoid;
  }
}
