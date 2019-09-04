/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

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
  protected TypeSymbol typeInfo;
  
  public TypeSymbol getTypeInfo() {
    return typeInfo;
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = typeInfo;
    typeSymbol = Optional.of(typeInfo); // TODO: for Compatibility; this can be deleted
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

  /**
   * If the Symbol corresponding to the type's name is loaded:
   * It is stored here.
   */
  @Deprecated
  protected Optional<TypeSymbol> typeSymbol = Optional.empty();

  // XXX BR: this list ist gefährlich, denn es ist nicht der Typ
  // Ausdruck der einen supertyp hat, sondern nur das TypSymbol
    // und das ist ja im Symbol gespeichert.
    // Konsequenz: muss man entfernen
    @Deprecated
  protected List<SymTypeExpression> superTypes = new ArrayList<>();
  
  @Deprecated
  private void lazyLoadTypeSymbol() {
    if(typeSymbol==null || !typeSymbol.isPresent())
      typeSymbol = enclosingScope.resolveType(this.name);
  }
  
  @Deprecated
  public TypeSymbol getTypeSymbol() {
    return typeInfo;
  }
  
  @Deprecated
  public void setTypeSymbol(Optional<TypeSymbol> x) {
    typeSymbol=x;setTypeInfo(x.get());
  }
  
  @Deprecated
  public String getName() {
    return name;
  }
  
  @Deprecated
  public void setName(String name) {
    this.name = name;
  }

  @Deprecated
  public TypeSymbolsScope getEnclosingScope() {
    return enclosingScope;
  }

  @Deprecated
  public void setEnclosingScope(TypeSymbolsScope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }


@Deprecated
  public List<SymTypeExpression> getSuperTypes() {
    return superTypes;
  }

@Deprecated
  public void setSuperTypes(List<SymTypeExpression> superTypes) {
    this.superTypes = superTypes;
  }

  @Deprecated
  public void addSuperType(SymTypeExpression superType){
    this.superTypes.add(superType);
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
