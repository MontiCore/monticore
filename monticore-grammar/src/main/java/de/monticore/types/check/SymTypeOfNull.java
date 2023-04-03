/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;

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
   * @deprecated contains no non-deprecated program logic
   */
  @Deprecated
  public SymTypeOfNull() {
    typeSymbol = new TypeSymbolSurrogate(BasicSymbolsMill.NULL);
    typeSymbol.setEnclosingScope(BasicSymbolsMill.scope());
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
      return BasicSymbolsMill.NULL;
  }

  @Override
  public String printFullName(){
    return print();
  }

  @Override
  public boolean isNullType() {
    return true;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    return sym.isNullType();
  }

  // --------------------------------------------------------------------------


}
