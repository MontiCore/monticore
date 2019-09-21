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
   * (is this needed?)
   */
  public boolean isPrimitiveType() {
    return false;
  }
  
  /**
   * Assumption:
   * We assume that each(!) and really each SymTypeExpression has
   * an associated TypeSymbol, where all available Fields, Methods, etc. can be found.
   *
   * These may, however, be empty, e.g. for primitive Types.
   *
   * Furthermore, each SymTypeExpression knows this TypeSymbol (i.e. the
   * TypeSymbols are loaded (or created) upon creation of the SymType.
   */
  protected Optional<TypeSymbol> typeInfo;
  
  public TypeSymbol getTypeInfo(ITypeSymbolsScope symbolTable) {
    if(typeInfo.isPresent()) {
      return typeInfo.get();
    }

    // TODO: in case of failure this is repeated each time ... may be inefficient
    // (but the memorization of a repeated load may be stored before looking at symtab files)
    typeInfo = symbolTable.resolveType(this.getName());
    // TODO: Error may occur? e.g. missing symbol. Was there already an error message?
    return typeInfo.get();
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = Optional.of(typeInfo);
  }
  
  // --------------------------------------------------------------------------

  /**
   * A type has a name (XXX BR Exceptions may apply?)
   * anonymous types only in List<?> in FullGenericTypes.mc4, not yet supported
   */
  protected String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
