package de.monticore.types2;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class SymTypeVoid extends SymTypeExpression {
  
  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "void" with no Fields, no Methods, etc.
   */
  private static TypeSymbol _voidTypeSymbol = null;
  
  public static TypeSymbol voidTypeSymbol() {
     if(_voidTypeSymbol == null) {
       _voidTypeSymbol = TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName("voidType")           // should be unused
            .setFullName("voidType")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setTypeParameter(Collections.emptyList())
            .setFields(Collections.emptyList())
            .setMethods(Collections.emptyList())
            .build();
     }
     return _voidTypeSymbol;
  }
  
  public SymTypeVoid() {
    setTypeInfo(voidTypeSymbol());
  }
  
  /**
     * print: Umwandlung in einen kompakten String
     */
  public String print() {
    return "void";
  }
  
    
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }
  
  @Override @Deprecated
  public SymTypeExpression deepClone() {
    return new SymTypeVoid();
  }
  
}
