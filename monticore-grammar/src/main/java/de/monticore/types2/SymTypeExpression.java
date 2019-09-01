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
   */
  public boolean isPrimitiveType() {
    return false;
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
  // XXX BR: unklar, ob das optional sein muss, wenn schon der Name
          // immer gesetzt ist und das hier auch immer geladen werden muss
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
    return typeSymbol.get();
  }
  
  @Deprecated
  public void setTypeSymbol(Optional<TypeSymbol> x) {
    typeSymbol=x;
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
