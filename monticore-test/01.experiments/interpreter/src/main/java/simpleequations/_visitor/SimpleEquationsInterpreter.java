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

  public Value interpret(ASTVariable node) {
    Value value = node.getValue().evaluate(getRealThis());
    getRealThis().store(node.getSymbol(), value);
    return new NotAValue();
  }

  public Value interpret(ASTPlusEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt()) {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asInt() + right.asInt());
      } else {
        return ValueFactory.createValue(left.asInt() + right.asFloat());
      }
    } else {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asFloat() + right.asInt());
      } else {
        return ValueFactory.createValue(left.asFloat() + right.asFloat());
      }
    }
  }

  public Value interpret(ASTMinusEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt()) {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asInt() - right.asInt());
      } else {
        return ValueFactory.createValue(left.asInt() - right.asFloat());
      }
    } else {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asFloat() - right.asInt());
      } else {
        return ValueFactory.createValue(left.asFloat() - right.asFloat());
      }
    }
  }

  public Value interpret(ASTMultiplyEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt()) {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asInt() * right.asInt());
      } else {
        return ValueFactory.createValue(left.asInt() * right.asFloat());
      }
    } else {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asFloat() * right.asInt());
      } else {
        return ValueFactory.createValue(left.asFloat() * right.asFloat());
      }
    }
  }

  public Value interpret(ASTDivideEquation node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt()) {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asInt() / right.asInt());
      } else {
        return ValueFactory.createValue(left.asInt() / right.asFloat());
      }
    } else {
      if (right.isInt()) {
        return ValueFactory.createValue(left.asFloat() / right.asInt());
      } else {
        return ValueFactory.createValue(left.asFloat() / right.asFloat());
      }
    }
  }

  public Value interpret(ASTNameExpression node) {
    return ValueFactory.createValue(node.getName());
  }

  public Value interpret(ASTNumberExpression node) {
    return node.getNumber().evaluate(getRealThis());
  }

}
