package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GenericTypeExpression extends TypeExpression {

  public Optional<ETypeSymbol> getWhoAmI() {
    return whoAmI;
  }

  public void setWhoAmI(Optional<ETypeSymbol> whoAmI) {
    this.whoAmI = whoAmI;
  }

  public List<TypeExpression> getArguments() {
    return arguments;
  }

  public void setArguments(List<TypeExpression> arguments) {
    this.arguments = arguments;
  }

  Optional<ETypeSymbol> whoAmI;

  List<TypeExpression> arguments = new LinkedList<>();



}
