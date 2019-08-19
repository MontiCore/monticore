/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeExpression {
  private String name;

  private TypeSymbolsScope enclosingScope;

  private Optional<TypeSymbol> typeSymbol = Optional.empty();

  private List<TypeExpression> subTypes = new ArrayList<>();

  private List<TypeExpression> superTypes = new ArrayList<>();

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

  public List<TypeExpression> getSubTypes() {
    return subTypes;
  }

  public List<TypeExpression> getSuperTypes() {
    return superTypes;
  }

  public void setSubTypes(List<TypeExpression> subTypes) {
    this.subTypes = subTypes;
  }

  public void setSuperTypes(List<TypeExpression> superTypes) {
    this.superTypes = superTypes;
  }

  public void addSuperType(TypeExpression superType){
    this.superTypes.add(superType);
  }

  public void addSubType(TypeExpression subType){
    this.subTypes.add(subType);
  }

  public String getBaseName() {
    String[] parts = this.name.split("\\.");
    return parts[parts.length - 1];
  }

  public boolean deepEquals(TypeExpression typeExpression){
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    if((this.enclosingScope!=null&&typeExpression.enclosingScope!=null)&&(!this.enclosingScope.equals(typeExpression.enclosingScope))){
      return false;
    }
    if(!this.typeSymbol.equals(typeExpression.typeSymbol)){
      return false;
    }
    if(!this.subTypes.equals(typeExpression.subTypes)){
      return false;
    }
    if(!this.superTypes.equals(typeExpression.superTypes)){
      return false;
    }
    return true;
  }

  public TypeExpression clone(){
    TypeExpression clone = new TypeExpression();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setSubTypes(this.subTypes);
    clone.setSuperTypes(this.superTypes);
    return clone;
  }
}
