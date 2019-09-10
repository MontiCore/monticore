package de.monticore.types2;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.ArrayList;
import java.util.Collections;

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
    setTypeInfo(nullTypeSymbol);
  }
  
  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "null" with no Fields, no Methods, etc.
   */
  public static final TypeSymbol nullTypeSymbol;
  
  static {
    nullTypeSymbol = TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName("nullType")           // should be unused
            .setFullName("nullType")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setTypeParameterList(Collections.emptyList())
            .setFieldList(new ArrayList<>())
            .setMethodList(new ArrayList<>())
            .build();
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
      return "nullType";
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
