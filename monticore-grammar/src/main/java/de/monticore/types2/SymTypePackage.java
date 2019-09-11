// (c) https://github.com/MontiCore/monticore

package de.monticore.types2;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.Collections;


public class SymTypePackage extends SymTypeExpression {


  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return "package";
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+print()+"\"";
  }


  // --------------------------------------------------------------------------

  @Override
  @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }

  @Override
  @Deprecated
  public SymTypeExpression deepClone() {
    return new SymTypePackage();
  }
}

