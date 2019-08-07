package de.monticore.typescalculator;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.Optional;

public class TypeExpression<E> {
  private String name;

  private TypeSymbolsScope enclosingScope;

  private Optional<TypeSymbol> typeSymbol = Optional.empty();

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

  public TypeSymbolsScope getEnclosingScope() {
    return enclosingScope;
  }

  public void setEnclosingScope(TypeSymbolsScope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }

  public String getBaseName() {
    String[] parts = this.name.split(".");
    return parts[parts.length-1];
  }
}
