/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * SymTypeOfGenerics stores any kind of TypeConstructor applied
 * to Arguments, such as Map< int,Person >
 * List<Person>, List< Set< List< a >>>.
 * This subsumes all kinds of generic Types from several of the
 * MC-Type grammars.
 */
public class SymTypeOfGenerics extends SymTypeExpression {
  
  /**
   * A SymTypeExpression has
   *    a name (representing a TypeConstructor) and
   *    a list of Type Expressions
   * This is always the full qualified name (i.e. including package)
   */
  protected String typeConstructorFullName;
  
  /**
   * List of arguments of a type constructor
   */
  protected List<SymTypeExpression> arguments = new LinkedList<>();
  
  /**
   * Symbol corresponding to the type constructors's name (if loaded???)
   */
  protected TypeSymbol objTypeConstructorSymbol;
  
  @Deprecated // XXX: remove funct. (because TypeSymbol missing)
  public SymTypeOfGenerics(String typeConstructorFullName, List<SymTypeExpression> arguments) {
    this.typeConstructorFullName = typeConstructorFullName;
    this.arguments = arguments;
  }


  public SymTypeOfGenerics(String typeConstructorFullName, List<SymTypeExpression> arguments,
                           TypeSymbol objTypeConstructorSymbol) {
    this.typeConstructorFullName = typeConstructorFullName;
    this.arguments = arguments;
    this.objTypeConstructorSymbol = objTypeConstructorSymbol;
  }
  
  public String getTypeConstructorFullName() {
    return typeConstructorFullName;
  }
  
  public void setTypeConstructorFullName(String typeConstructorFullName) {
    this.typeConstructorFullName = typeConstructorFullName;
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
    StringBuffer r = new StringBuffer(getTypeConstructorFullName()).append('<');
    for(int i = 0; i<arguments.size();i++){
      r.append(arguments.get(i).print());
      if(i<arguments.size()-1) { r.append(','); }
    }
    return r.append('>').toString();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    //TODO: anpassen, nachdem package umbenannt ist
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeOfGenerics");
    jp.member("typeConstructorFullName", getTypeConstructorFullName());
    jp.beginArray("arguments");
    for(SymTypeExpression exp : getArguments()) {
      jp.valueJson(exp.printAsJson());
    }
    jp.endArray();
    //TODO: TypeSymbolDeSer implementieren
    jp.member("objTypeConstructorSymbol", "TODO");//new TypeSymbolDeSer().serialize(getObjTypeConstructorSymbol()));
    jp.endObject();
    return jp.getContent();
  }
  
  
  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getTypeConstructorFullName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getTypeConstructorFullName().split("\\.");
    return parts[parts.length - 1];
  }
  
  // --------------------------------------------------------------------------
  
  
  @Deprecated
  Optional<TypeSymbol> whoAmI;
  
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
  public List<SymTypeExpression> getArguments() {
    return arguments;
  }

  @Deprecated
  public void setArguments(List<SymTypeExpression> arguments) {
    this.arguments = arguments;
    Lists.newArrayList();
  }
  
  @Deprecated
  public void addArgument(SymTypeExpression argument){
    this.arguments.add(argument);
  }


  @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeOfGenerics)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    if(!this.typeInfo.equals(symTypeExpression.typeInfo)){
      return false;

    }
    for(int i = 0; i<this.arguments.size();i++){
      if(!this.arguments.get(i).deepEquals(((SymTypeOfGenerics) symTypeExpression).arguments.get(i))){
        return false;
      }
    }
    if(!this.whoAmI.equals(((SymTypeOfGenerics) symTypeExpression).whoAmI)){
      return false;
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeOfGenerics clone = new SymTypeOfGenerics();
    clone.setName(this.name);
    for(SymTypeExpression expr: arguments){
      clone.addArgument(expr.deepClone());
    }
    clone.typeInfo = this.typeInfo;
    clone.whoAmI = this.whoAmI;
    return clone;
  }
  
  @Deprecated
  public SymTypeOfGenerics() {
  }
  
}
