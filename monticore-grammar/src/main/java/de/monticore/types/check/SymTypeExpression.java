/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.ArrayList;
import java.util.LinkedList;
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
   * (default: no)
   */
  public boolean isPrimitive() {
    return false;
  }
  
  /**
   * Constraint:
   * We assume that each(!) and really each SymTypeExpression has
   * an associated TypeSymbol, where all available Fields, Methods, etc. can be found.
   *
   * These may, however, be empty, e.g. for primitive Types.
   *
   * Furthermore, each SymTypeExpression knows this TypeSymbol (i.e. the
   * TypeSymbols are loaded (or created) upon creation of the SymType.
   */
  protected TypeSymbol typeInfo;
  
  public TypeSymbol getTypeInfo() {
      return typeInfo;
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = typeInfo;
  }
  
  // --------------------------------------------------------------------------

  /**
   * TODO 4BR
   */
  @Deprecated // and unused .. can be deleted
  protected String name;

  @Deprecated
  public String getName() {
    return name;
  }

  @Deprecated
  public void setName(String name) {
    this.name = name;
  }
}
