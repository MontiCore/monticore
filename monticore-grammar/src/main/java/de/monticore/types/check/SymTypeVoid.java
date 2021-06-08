/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;


import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;

public class SymTypeVoid extends SymTypeExpression {
  
  public SymTypeVoid() {
    typeSymbol = new TypeSymbolSurrogate(BasicSymbolsMill.VOID);
  }
  
  /**
     * print: Umwandlung in einen kompakten String
     */
  @Override
  public String print() {
    return "void";
  }

  @Override
  public String printFullName() {
    return print();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+BasicSymbolsMill.VOID + "\"";
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
