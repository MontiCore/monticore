/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GenericTypeExpression extends TypeExpression {
  //String name;
  public Optional<TypeSymbol> getWhoAmI() {
    return whoAmI;
  }

  public void setWhoAmI(Optional<TypeSymbol> whoAmI) {
    this.whoAmI = whoAmI;
  }

  public List<TypeExpression> getArguments() {
    return arguments;
  }

  public void setArguments(List<TypeExpression> arguments) {
    this.arguments = arguments;
    Lists.newArrayList();
  }

  public void addArgument(TypeExpression argument){
    this.arguments.add(argument);
  }

  Optional<TypeSymbol> whoAmI = Optional.empty();

  List<TypeExpression> arguments = new LinkedList<>();


  @Override
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
    if(!this.subTypes.equals(typeExpression.subTypes)){
      return false;
    }
    if(!this.superTypes.equals(typeExpression.superTypes)){
      return false;
    }
    if(!this.arguments.equals(((GenericTypeExpression) typeExpression).arguments)){
      return false;
    }
    if(!this.whoAmI.equals(((GenericTypeExpression) typeExpression).whoAmI)){
      return false;
    }
    return true;
  }

  @Override
  public TypeExpression deepClone() {
    GenericTypeExpression clone = new GenericTypeExpression();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(TypeExpression expr: subTypes){
      clone.addSubType(expr.deepClone());
    }
    for(TypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    for(TypeExpression expr: arguments){
      clone.addArgument(expr.deepClone());
    }
    clone.whoAmI = this.whoAmI;
    return clone;
  }
}
