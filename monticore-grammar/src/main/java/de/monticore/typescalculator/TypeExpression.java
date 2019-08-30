/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TypeExpression is the superclass for all typeexpressions, such as
 * TypeConstants, TypeVariables and applications of Type-Constructors.
 * It shares common functionality
 * (such as comparison, printing)
 * TODO: later
 */
public abstract class TypeExpression {




  /**
   * A type has a name (XXX BR Exceptions may apply?)
   */
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
  protected Optional<TypeSymbol> typeSymbol = Optional.empty();

  // XXX BR: this list ist gefährlich, denn es ist nicht der Typ
  // Ausdruck der einen supertyp hat, sondern nur das TypSymbol
    // und das ist ja im Symbol gespeichert.
    // Konsequenz: muss man entfernen
    @Deprecated
  protected List<TypeExpression> superTypes = new ArrayList<>();

    @Deprecated
  private void lazyLoadTypeSymbol() {
    if(typeSymbol==null || !typeSymbol.isPresent())
      typeSymbol = enclosingScope.resolveType(this.name);
  }

  public TypeSymbol getTypeSymbol() {
    return typeSymbol.get();
  }

  public String getName() {
    return name;
  }

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
  public List<TypeExpression> getSuperTypes() {
    return superTypes;
  }

@Deprecated
  public void setSuperTypes(List<TypeExpression> superTypes) {
    this.superTypes = superTypes;
  }

  @Deprecated
  public void addSuperType(TypeExpression superType){
    this.superTypes.add(superType);
  }


  public String getBaseName() {
    String[] parts = this.name.split("\\.");
    return parts[parts.length - 1];
  }

  abstract public boolean deepEquals(TypeExpression typeExpression);

  abstract public TypeExpression deepClone();
}
