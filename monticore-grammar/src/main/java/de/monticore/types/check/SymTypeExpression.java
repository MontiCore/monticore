/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SymTypeExpression is the superclass for all typeexpressions, such as
 * TypeConstants, TypeVariables and applications of Type-Constructors.
 * It shares common functionality
 * (such as comparison, printing)
 */
public abstract class SymTypeExpression {

  /**
   * print: Umwandlung in einen kompakten String
   */
  public abstract String print();
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected abstract String printAsJson();
  
  /**
   * Am I primitive? (such as "int")
   * (is this needed?)
   */
  public boolean isPrimitiveType() {
    return false;
  }
  
  /**
   * Assumption:
   * We assume that each(!) and really each SymTypeExpression has
   * an associated TypeSymbol, where all Fields, Methods, etc. are stored.
   *
   * These Lists may, however, be empty e.g. for primitive Types.
   *
   * Furthermore, each SymType knows this TypeSymbol (i.e. the
   * TypeSymbols are loaded (or created) upon creation of the SymType.
   */
  protected Optional<TypeSymbol> typeInfo;
  
  public TypeSymbol getTypeInfo(ITypeSymbolsScope symbolTable) {
    if(typeInfo.isPresent()) {
      return typeInfo.get();
    }

    typeInfo = symbolTable.resolveType(this.getName());
    return typeInfo.get();
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = Optional.of(typeInfo);
  }
  
  // --------------------------------------------------------------------------

  /**
   * A type has a name (XXX BR Exceptions may apply?)
   */
  @Deprecated
  protected String name;

  /// XXX BR: das hat hier nichts zu suchen
  @Deprecated
  protected TypeSymbolsScope enclosingScope;



  
  @Deprecated
  public String getName() {
    return name;
  }
  
  @Deprecated
  public void setName(String name) {
    this.name = name;
  }



  
  
  @Deprecated // aber nur in der Basisklasse (manche Subklassen behalten dies)
  public String getBaseName() {
    String[] parts = this.name.split("\\.");
    return parts[parts.length - 1];
  }
  
  @Deprecated
  abstract public boolean deepEquals(SymTypeExpression symTypeExpression);
  
  @Deprecated
  abstract public SymTypeExpression deepClone();
}
