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

  Optional<TypeSymbol> whoAmI;

  List<TypeExpression> arguments = new LinkedList<>();



}
