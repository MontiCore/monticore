/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.interpreter.ValueFactory.createValue;

public class CommonExpressionsInterpreter extends CommonExpressionsInterpreterTOP {

  public CommonExpressionsInterpreter() {
    super();
  }

  public CommonExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public Value interpret(ASTPlusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      return createValue(left.asString() + right.asString());
    } else if (left.isBoolean() || right.isBoolean() || left.isObject() || right.isObject()) {
      Log.error("Plus operation is not applicable for these types.");
    } else if (left.isDouble() || right.isDouble()) {
      return createValue(left.asDouble() + right.asDouble());
    } else if (left.isFloat() || right.isFloat()) {
      return createValue(left.asFloat() + right.asFloat());
    } else if (left.isLong() || right.isLong()) {
      return createValue(left.asLong() + right.asLong());
    } else if (left.isInt() || right.isInt() || left.isChar() || right.isChar()) {
      return createValue(left.asInt() + right.asInt());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTMinusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    return compare(left, right);
  }

  @Override
  public Value interpret(ASTMultExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString() || left.isBoolean() ||
        right.isBoolean() || left.isObject() || right.isObject()) {
      Log.error("Minus operation is not applicable for these types.");
    } else if (left.isDouble() || right.isDouble()) {
      return createValue(left.asDouble() * right.asDouble());
    } else if (left.isFloat() || right.isFloat()) {
      return createValue(left.asFloat() * right.asFloat());
    } else if (left.isLong() || right.isLong()) {
      return createValue(left.asLong() * right.asLong());
    } else if (left.isInt() || right.isInt() || left.isChar() || right.isChar()) {
      return createValue(left.asInt() * right.asInt());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTDivideExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString() || left.isBoolean() ||
        right.isBoolean() || left.isObject() || right.isObject()) {
      Log.error("Minus operation is not applicable for these types.");
      return new NotAValue();
    }

    if (right.asDouble() == 0) {
      Log.error("Division by Zero is not defined.");
      return new NotAValue();
    }

    if (left.isDouble() || right.isDouble()) {
      return createValue(left.asDouble() / right.asDouble());
    } else if (left.isFloat() || right.isFloat()) {
      return createValue(left.asFloat() / right.asFloat());
    } else if (left.isLong() || right.isLong()) {
      return createValue(left.asLong() / right.asLong());
    } else if (left.isInt() || right.isInt() || left.isChar() || right.isChar()) {
      return createValue(left.asInt() / right.asInt());
    }

    return new NotAValue();
  }

  @Override
  public Value interpret(ASTModuloExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString() || left.isBoolean() ||
        right.isBoolean() || left.isObject() || right.isObject()) {
      Log.error("Minus operation is not applicable for these types.");
    } else if (left.isDouble() || right.isDouble()) {
      return createValue(left.asDouble() % right.asDouble());
    } else if (left.isFloat() || right.isFloat()) {
      return createValue(left.asFloat() % right.asFloat());
    } else if (left.isLong() || right.isLong()) {
      return createValue(left.asLong() % right.asLong());
    } else if (left.isInt() || right.isInt() || left.isChar() || right.isChar()) {
      return createValue(left.asInt() % right.asInt());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTMinusPrefixExpression node) {
    Value expr = node.getExpression().evaluate(getRealThis());

    if (expr.isObject() || expr.isBoolean() || expr.isString()) {
      Log.error("Minus Prefix operation is not applicable for these types.");
    } else if (expr.isInt()) {
      return createValue(-expr.asInt());
    } else if (expr.isLong()) {
      return createValue(-expr.asLong());
    } else if (expr.isDouble()) {
      return createValue(-expr.asDouble());
    } else if (expr.isFloat()) {
      return createValue(-expr.asFloat());
    } else if (expr.isChar()) {
      return createValue(-expr.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTPlusPrefixExpression node) {
    Value expr = node.getExpression().evaluate(getRealThis());

    if (expr.isObject() || expr.isBoolean() || expr.isString()) {
      Log.error("Plus Prefix operation is not applicable for these types.");
    } else if (expr.isInt()) {
      return createValue(expr.asInt());
    } else if (expr.isLong()) {
      return createValue(+expr.asLong());
    } else if (expr.isDouble()) {
      return createValue(+expr.asDouble());
    } else if (expr.isFloat()) {
      return createValue(+expr.asFloat());
    } else if (expr.isChar()) {
      return createValue(+expr.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() == right.asBoolean());
    }
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(result.asDouble() == 0);
  }

  @Override
  public Value interpret(ASTNotEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() != right.asBoolean());
    }
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(compare(left, right).asDouble() != 0);
  }

  @Override
  public Value interpret(ASTGreaterThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(compare(left, right).asDouble() > 0);
  }

  @Override
  public Value interpret(ASTLessThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(compare(left, right).asDouble() < 0);
  }

  @Override
  public Value interpret(ASTGreaterEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(compare(left, right).asDouble() >= 0);
  }

  @Override
  public Value interpret(ASTLessEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    Value result = compare(left, right);
    return result instanceof NotAValue ? result : createValue(compare(left, right).asDouble() <= 0);
  }

  public Value compare(Value left, Value right) {
    if (left.isString() || right.isString() || left.isBoolean() ||
        right.isBoolean() || left.isObject() || right.isObject()) {
      Log.error("Operation is not applicable for these types.");
    } else if (left.isDouble() || right.isDouble()) {
      return createValue(left.asDouble() - right.asDouble());
    } else if (left.isFloat() || right.isFloat()) {
      return createValue(left.asFloat() - right.asFloat());
    } else if (left.isLong() || right.isLong()) {
      return createValue(left.asLong() - right.asLong());
    } else if (left.isInt() || right.isInt() || left.isChar() || right.isChar()) {
      return createValue(left.asInt() - right.asInt());
    }
    return new NotAValue();
  }

  //~ -> behaves as a bitwise complement
  @Override
  public Value interpret(ASTBooleanNotExpression node) {
    Value res = node.getExpression().evaluate(getRealThis());

    if (res.isFloat() || res.isObject() || res.isString() || res.isDouble() || res.isBoolean()) {
      Log.error("Logical Not operation is not applicable for these types.");
    } else if (res.isChar()) {
      return createValue(~res.asChar());
    } else if (res.isInt()) {
      return createValue(~res.asInt());
    } else if (res.isLong()) {
      return createValue(~res.asLong());
    }
    return new NotAValue();
  }

  /*=================================================================*/
  //Logical and boolean operations
  /*=================================================================*/

  @Override
  public Value interpret(ASTLogicalNotExpression node) {
    Value res = node.getExpression().evaluate(getRealThis());

    if (res.isBoolean()) {
      return createValue(!res.asBoolean());
    }
    Log.error("Logical Not operation is not applicable for these types.");
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTBooleanAndOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() && right.asBoolean());
    }
    Log.error("Logical And operation is not applicable.");
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTBooleanOrOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() || right.asBoolean());
    }
    Log.error("Logical Or operation is not applicable.");
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTBracketExpression node) {
    return node.getExpression().evaluate(getRealThis());
  }

  @Override
  public Value interpret(ASTConditionalExpression node) {
    Value condition = node.getCondition().evaluate(getRealThis());
    if (!condition.isBoolean()) {
      Log.error("Condition of Ternary Operator has to be a Boolean Type");
      return new NotAValue();
    }
    return condition.asBoolean()
        ? node.getTrueExpression().evaluate(getRealThis())
        : node.getFalseExpression().evaluate(getRealThis());
  }

  @Override
  public Value interpret(ASTFieldAccessExpression node) {
    String expression = CommonExpressionsMill.prettyPrint(node, false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveVariable(expression);
    return symbol.map(this::load).orElse(new NotAValue());
  }

  @Override
  public Value interpret(ASTLiteralExpression node) {
    return node.getLiteral().evaluate(getRealThis());
  }
}