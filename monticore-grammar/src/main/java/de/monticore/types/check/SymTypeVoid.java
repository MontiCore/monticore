package de.monticore.types.check;


import de.monticore.types.typesymbols._symboltable.BuiltInJavaTypeSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

public class SymTypeVoid extends SymTypeExpression {
  
  public SymTypeVoid() {
    typeSymbolLoader = new TypeSymbolLoader(DefsTypeBasic._voidTypeString,
        BuiltInJavaTypeSymbolResolvingDelegate.getScope());
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
