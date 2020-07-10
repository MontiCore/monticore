/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

public class SymTypeOfNull extends SymTypeExpression {

  /**
   * This Class represents the type of the value "null".
   * This type doesn't really exist (hence the print method delivers "nullType", i.e. _nullTypeString),
   * but the object is used to attach "null" a proper type,
   * which is then compatible to e.g. to TypeConstant or TypeArray,
   *       int[] j = null;        ok
   *       Integer i2 = null;     ok
   * but not e.g. to int
   *       int i = null;          illegal
   */
  public SymTypeOfNull() {
//    typeSymbolSurrogate = new TypeSymbolSurrogate(DefsTypeBasic._nullTypeString,
//        BuiltInJavaTypeSymbolResolvingDelegate.getScope());
    typeSymbolSurrogate = new PseudoTypeSymbolSurrogate(DefsTypeBasic._null);
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
      return DefsTypeBasic._nullTypeString;
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+DefsTypeBasic._nullTypeString+"\"";
  }

  @Override
  public SymTypeOfNull deepClone() {
    return new SymTypeOfNull();
  }

  @Override
  public boolean isNullType() {
    return true;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    return sym instanceof SymTypeOfNull;
  }

  // --------------------------------------------------------------------------


}
