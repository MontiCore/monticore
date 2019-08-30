/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GenericTypeExpression extends TypeExpression {
  
  /**
   * A TypeExpression has
   *    a name (representing a TypeConstructor) and
   *    a list of Type Expressions
   * This is always the full qualified name (i.e. including package)
   */
  protected String typeConstructorName;
  
  /**
   * List of arguments of a type constructor
   */
  List<TypeExpression> arguments = new LinkedList<>();
  
  /**
   * Symbol corresponding to the type constructors's name (if loaded???)
   */
  // XXX BR: unklar, ob das optional sein muss, wenn schon der Name
  // immer gesetzt ist; man könnte das Symbol gleich beim initialisieren mit setzen lassen
  protected TypeSymbol objTypeConstructorSymbol;
  
  public ObjectType(String typeConstructorName) {
    this.typeConstructorName = typeConstructorName;
  }
  
  public String getTypeConstructorName() {
    return typeConstructorName;
  }
  
  public void setTypeConstructorName(String typeConstructorName) {
    this.typeConstructorName = typeConstructorName;
  }
  
  public TypeSymbol getObjTypeConstructorSymbol() {
    return objTypeConstructorSymbol;
  }
  
  public void setObjTypeConstructorSymbol(TypeSymbol objTypeConstructorSymbol) {
    this.objTypeConstructorSymbol = objTypeConstructorSymbol;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    String r = getTypeConstructorName() + "<";
    arguments.forEach((a) -> r += a);
    return r +">";
  }
  
  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getTypeConstructorName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getTypeConstructorName().split("\\.");
    return parts[parts.length - 1];
  }
  
  // --------------------------------------------------------------------------
  
  
  
  
  //String name;
  @Deprecated
  public Optional<TypeSymbol> getWhoAmI() {
    return whoAmI;
  }

  @Deprecated
  public void setWhoAmI(Optional<TypeSymbol> whoAmI) {
    this.whoAmI = whoAmI;
  }

  @Deprecated
  public List<TypeExpression> getArguments() {
    return arguments;
  }

  @Deprecated
  public void setArguments(List<TypeExpression> arguments) {
    this.arguments = arguments;
    Lists.newArrayList();
  }
  
  @Deprecated
  public void addArgument(TypeExpression argument){
    this.arguments.add(argument);
  }


  @Override @Deprecated
  public boolean deepEquals(TypeExpression typeExpression) {
    if(!(typeExpression instanceof GenericTypeExpression)){
      return false;
    }
    if(!this.name.equals(typeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(typeExpression.typeSymbol)){
      return false;
    }

    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(typeExpression.superTypes.get(i))){
        return false;
      }
    }
    for(int i = 0; i<this.arguments.size();i++){
      if(!this.arguments.get(i).deepEquals(((GenericTypeExpression) typeExpression).arguments.get(i))){
        return false;
      }
    }
    if(!this.whoAmI.equals(((GenericTypeExpression) typeExpression).whoAmI)){
      return false;
    }
    return true;
  }

  @Override @Deprecated
  public TypeExpression deepClone() {
    GenericTypeExpression clone = new GenericTypeExpression();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);

    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    for(TypeExpression expr: arguments){
      clone.addArgument(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    clone.whoAmI = this.whoAmI;
    return clone;
  }
}
