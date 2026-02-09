/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.values.ErrorMIValue;
import simpleequations._ast.*;

import java.util.Optional;

public class SimpleEquationsInterpreter extends SimpleEquationsInterpreterTOP {

  public SimpleEquationsInterpreter() {
    super();
  }

  public MIValue interpret(ASTProgram node) {
    node.forEachStatements(s -> s.evaluate(getRealThis()));
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    }
    return new ErrorMIValue("Error ASTProgram node");
  }

  public MIValue interpret(ASTPlusEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
        return MIValueFactory.createValue(left.asInt() + right.asInt());
      }
    return MIValueFactory.createValue(left.asFloat() + right.asFloat());
  }

  public MIValue interpret(ASTMinusEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() - right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() - right.asFloat());
  }

  public MIValue interpret(ASTMultiplyEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() * right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() * right.asFloat());
  }

  public MIValue interpret(ASTDivideEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() / right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() / right.asFloat());
  }

  public MIValue interpret(ASTVariableDefinition node) {
    MIValue value = node.getValue().evaluate(getRealThis());
    getRealThis().storeVariable(node.getSymbol(), value);
    return new ErrorMIValue("Error ASTVariableDefinition node");
  }

  public MIValue interpret(ASTVariableUsage node) {
    var symbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());
    MIValue value = node.getValue().evaluate(getRealThis());
    symbol.ifPresent(s -> getRealThis().storeVariable(s, value));
    return new ErrorMIValue("Error ASTVariableUsage node");
  }

  public MIValue interpret(ASTPrintStatement node) {
    MIValue output = node.getExpression().evaluate(getRealThis());

    if (output.isInt()) {
      System.out.println(output.asInt());
    } else if (output.isFloat()) {
      System.out.println(output.asFloat());
    }
    return new ErrorMIValue("Error ASTPrintStatement node");
  }

  public MIValue interpret(ASTNameExpression node) {
    var optSymbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());
    if (optSymbol.isPresent()) {
      return getRealThis().loadVariable(optSymbol.get());
    }
    return new ErrorMIValue("Error ASTNameExpression node");
  }

  public MIValue interpret(ASTNumberExpression node) {
    return node.getNumber().evaluate(getRealThis());
  }

}
