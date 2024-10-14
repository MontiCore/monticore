/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.NotAValue;
import simpleequations._ast.*;

public class SimpleEquationsInterpreter extends SimpleEquationsInterpreterTOP {

  public SimpleEquationsInterpreter() {
    super();
  }

  public Value interpret(ASTProgram node) {
    node.forEachStatements(s -> s.evaluate(getRealThis()));
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    }
    return new NotAValue();
  }

  public Value interpret(ASTPlusEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() + right.asInt());
      }
    return ValueFactory.createValue(left.asFloat() + right.asFloat());
  }

  public Value interpret(ASTMinusEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return ValueFactory.createValue(left.asInt() - right.asInt());
    }
    return ValueFactory.createValue(left.asFloat() - right.asFloat());
  }

  public Value interpret(ASTMultiplyEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return ValueFactory.createValue(left.asInt() * right.asInt());
    }
    return ValueFactory.createValue(left.asFloat() * right.asFloat());
  }

  public Value interpret(ASTDivideEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return ValueFactory.createValue(left.asInt() / right.asInt());
    }
    return ValueFactory.createValue(left.asFloat() / right.asFloat());
  }

  public Value interpret(ASTVariableDefinition node) {
    Value value = node.getValue().evaluate(getRealThis());
    getRealThis().store(node.getSymbol(), value);
    return new NotAValue();
  }

  public Value interpret(ASTVariableUsage node) {
    var symbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());
    Value value = node.getValue().evaluate(getRealThis());
    symbol.ifPresent(s -> getRealThis().store(s, value));
    return new NotAValue();
  }

  public Value interpret(ASTPrintStatement node) {
    Value output = node.getExpression().evaluate(getRealThis());

    if (output.isInt()) {
      System.out.println(output.asInt());
    } else if (output.isFloat()) {
      System.out.println(output.asFloat());
    }
    return new NotAValue();
  }

  public Value interpret(ASTNameExpression node) {
    var optSymbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());
    return optSymbol.map(getRealThis()::load).orElse(new NotAValue());
  }

  public Value interpret(ASTNumberExpression node) {
    return node.getNumber().evaluate(getRealThis());
  }

}
