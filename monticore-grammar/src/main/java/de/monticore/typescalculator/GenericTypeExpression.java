package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GenericTypeExpression extends TypeExpression {

  Optional<ETypeSymbol> whoAmI;

  List<TypeExpression> arguments = new LinkedList<>();



}
