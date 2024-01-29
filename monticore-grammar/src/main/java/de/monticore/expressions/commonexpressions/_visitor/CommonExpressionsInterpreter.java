/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class CommonExpressionsInterpreter extends CommonExpressionsInterpreterTOP {

  public CommonExpressionsInterpreter() {
    super();
  }

  public CommonExpressionsInterpreter(de.monticore.interpreter.ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public Value interpret(ASTPlusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      if (left.isInt()) {
        return ValueFactory.createValue(left.asInt() + right.asString());
      } else if (right.isInt()) {
        return ValueFactory.createValue(left.asString() + right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() + right.asString());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asString() + right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asString());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asString() + right.asChar());
      } else if (left.isBoolean()) {
        return ValueFactory.createValue(left.asBoolean() + right.asString());
      } else if (right.isBoolean()) {
        return ValueFactory.createValue(left.asString() + right.asBoolean());
      } else if (left.isString() && right.isString()) {
        return ValueFactory.createValue(left.asString() + right.asString());
      } else if (left.isObject()) {
        return ValueFactory.createValue(left.asObject() + right.asString());
      } else if (right.isObject()) {
        return ValueFactory.createValue(left.asString() + right.asObject());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() + right.asString());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asString() + right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() + right.asString());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asString() + right.asLong());
      }
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() + right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() + right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() + right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() + right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() + right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() + right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() + right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() + right.asFloat());
      } else {
        Log.error("Plus operation is not applicable for these types.");
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() + right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() + right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() + right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() + right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() + right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() + right.asFloat());
      } else {
        Log.error("Plus operation is not applicable for these types.");
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() + right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() + right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() + right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() + right.asChar());
      } else {
        Log.error("Plus operation is not applicable for these types.");
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() + right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() + right.asChar());
      } else {
        Log.error("Plus operation is not applicable for these types.");
      }
    } else if (left.isChar() || right.isChar()) {
      if (left.isChar() && right.isChar()) {
        return ValueFactory.createValue(left.asChar() + right.asChar());
      }
      Log.error("Plus operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Plus operation is not applicable for these types.");
    } else if (left.isObject() && right.isObject()) {
      Log.error("Plus operation is not applicable for these types.");
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTMinusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Minus operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Minus operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Minus operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() - right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() - right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() - right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() - right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() - right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() - right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() - right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() - right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() - right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() - right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() - right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() - right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() - right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() - right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() - right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() - right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() - right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() - right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() - right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() - right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() - right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() - right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() - right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() - right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() - right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTMultExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Multiplication operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Multiplication operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Multiplication operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() * right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() * right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() * right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() * right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() * right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() * right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() * right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() * right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() * right.asFloat());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() * right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() * right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() * right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() * right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() * right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() * right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() * right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() * right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() * right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() * right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() * right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() * right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() * right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() * right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() * right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() * right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTDivideExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Division operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Division operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Division operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        if (right.asInt() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asInt() / right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() / right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() / right.asDouble());
      } else if (left.isChar()) {
        if (right.asInt() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asChar() / right.asInt());
      } else if (right.isChar()) {
        if (right.asInt() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asInt() / right.asChar());
      } else if (left.isLong()) {
        if (right.asInt() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asLong() / right.asInt());
      } else if (right.isLong()) {
        if (right.asLong() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asInt() / right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() / right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() / right.asFloat());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() / right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() / right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() / right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() / right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() / right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() / right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() / right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        if (right.asLong() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asLong() / right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() / right.asLong());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asLong() / right.asFloat());
      } else if (left.isChar()) {
        if (right.asLong() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asChar() / right.asLong());
      } else if (right.isChar()) {
        if (right.asChar() == 0) {
          Log.error("Division by 0 is not supported.");
          return new NotAValue();
        }
        return ValueFactory.createValue(left.asLong() / right.asChar());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() / right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() / right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() / right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      if (right.asChar() == 0) {
        Log.error("Division by 0 is not supported.");
        return new NotAValue();
      }
      return ValueFactory.createValue(left.asChar() / right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTModuloExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Modulo operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Modulo operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Modulo operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() % right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() % right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() % right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() % right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() % right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() % right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() % right.asLong());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() % right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() % right.asFloat());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() % right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() % right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() % right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() % right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() % right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() % right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() % right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() % right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() % right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() % right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() % right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() % right.asChar());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() % right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() % right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() % right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() % right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTMinusPrefixExpression node) {
    Value expr = node.getExpression().evaluate(getRealThis());

    if (expr.isObject() || expr.isBoolean() || expr.isString()) {
      Log.error("Minus Prefix operation is not applicable for these types.");
    } else if (expr.isInt()) {
      return ValueFactory.createValue(-expr.asInt());
    } else if (expr.isLong()) {
      return ValueFactory.createValue(-expr.asLong());
    } else if (expr.isDouble()) {
      return ValueFactory.createValue(-expr.asDouble());
    } else if (expr.isFloat()) {
      return ValueFactory.createValue(-expr.asFloat());
    } else if (expr.isChar()) {
      return ValueFactory.createValue(-expr.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() || right.isInt()) {
      if (left.isString() || right.isString()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() == right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() == right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() == right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() == right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() == right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() == right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() == right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() == right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() == right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isString() || right.isString()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() == right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() == right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() == right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() == right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() == right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() == right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() == right.asLong());
      }
    } else if (left.isChar() || right.isChar()) {
      if (left.isString() || right.isString()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isChar() && right.isChar()) {
        return ValueFactory.createValue(left.asChar() == right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() == right.asChar());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asChar() == right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() == right.asChar());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asChar() == right.asLong());
      }
    } else if (left.isString() || right.isString()) {
      if (left.isBoolean() || right.isBoolean()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isLong() || right.isLong()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isFloat() || right.isFloat()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isString() && right.isString()) {
        return ValueFactory.createValue(left.asString().equals(right.asString()));
      }
    } else if (left.isBoolean() || right.isBoolean()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isFloat() || right.isFloat()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isLong() || right.isLong()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isBoolean() && right.isBoolean()) {
        return ValueFactory.createValue(left.asBoolean() == right.asBoolean());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() == right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() == right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() == right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Equals operation is not applicable for these types.");
      } else if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() == right.asLong());
      }
    } else if (left.isObject() && right.isObject()) {
      return ValueFactory.createValue(left.asObject() == right.asObject());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTNotEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isInt() || right.isInt()) {
      if (left.isString() || right.isString()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() != right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() != right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() != right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() != right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() != right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() != right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() != right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() != right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() != right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isString() || right.isString()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() != right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() != right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() != right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() != right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() != right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() != right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() != right.asLong());
      }
    } else if (left.isChar() || right.isChar()) {
      if (left.isString() || right.isString()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isBoolean() || right.isBoolean()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isChar() && right.isChar()) {
        return ValueFactory.createValue(left.asChar() != right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() != right.asChar());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asChar() != right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() != right.asChar());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asChar() != right.asLong());
      }
    } else if (left.isString() || right.isString()) {
      if (left.isBoolean() || right.isBoolean()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isLong() || right.isLong()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isFloat() || right.isFloat()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isString() && right.isString()) {
        return ValueFactory.createValue(!left.asString().equals(right.asString()));
      }
    } else if (left.isBoolean() || right.isBoolean()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isLong() || right.isLong()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isFloat() || right.isFloat()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isBoolean() && right.isBoolean()) {
        return ValueFactory.createValue(left.asBoolean() != right.asBoolean());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      } else if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() != right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() != right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() != right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isObject() || right.isObject()) {
        Log.error("Not Equals operation is not applicable for these types.");
      }
      return ValueFactory.createValue(left.asLong() != right.asLong());
    } else if (left.isObject() && right.isObject()) {
      return ValueFactory.createValue(left.asObject() != right.asObject());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTGreaterThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Greater Than operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Greater Than operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Greater Than operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() > right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() > right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() > right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() > right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() > right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() > right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() > right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() > right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() > right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() > right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() > right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() > right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() > right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() > right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() > right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() > right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() > right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() > right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() > right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() > right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() > right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() > right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() > right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() > right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() > right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTLessThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Less Than operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Less Than operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Less Than operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() < right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() < right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() < right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() < right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() < right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() < right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() < right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() < right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() < right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() < right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() < right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() < right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() < right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() < right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() < right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() < right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() < right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() < right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() < right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() < right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() < right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() < right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() < right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() < right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() < right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTGreaterEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Greater Equal operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Greater Equal operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Greater Equal operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() >= right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() >= right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() >= right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() >= right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() >= right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() >= right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() >= right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() >= right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() >= right.asLong());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() >= right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() >= right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() >= right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() >= right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() >= right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() >= right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() >= right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() >= right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() >= right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() >= right.asChar());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() >= right.asFloat());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asFloat() >= right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() >= right.asLong());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() >= right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() >= right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() >= right.asChar());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTLessEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isString() || right.isString()) {
      Log.error("Less Equal operation is not applicable for these types.");
    } else if (left.isBoolean() || right.isBoolean()) {
      Log.error("Less Equal operation is not applicable for these types.");
    } else if (left.isObject() || right.isObject()) {
      Log.error("Less Equal operation is not applicable for these types.");
    } else if (left.isInt() || right.isInt()) {
      if (left.isInt() && right.isInt()) {
        return ValueFactory.createValue(left.asInt() <= right.asInt());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() <= right.asInt());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asInt() <= right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() <= right.asInt());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asInt() <= right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() <= right.asInt());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asInt() <= right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() <= right.asInt());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asInt() <= right.asLong());
      }
    } else if (left.isLong() || right.isLong()) {
      if (left.isLong() && right.isLong()) {
        return ValueFactory.createValue(left.asLong() <= right.asLong());
      } else if (left.isDouble()) {
        return ValueFactory.createValue(left.asDouble() <= right.asLong());
      } else if (right.isDouble()) {
        return ValueFactory.createValue(left.asLong() <= right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() <= right.asLong());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asLong() <= right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() <= right.asLong());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asLong() <= right.asFloat());
      }
    } else if (left.isDouble() || right.isDouble()) {
      if (left.isDouble() && right.isDouble()) {
        return ValueFactory.createValue(left.asDouble() <= right.asDouble());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() <= right.asDouble());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asDouble() <= right.asChar());
      } else if (left.isFloat()) {
        return ValueFactory.createValue(left.asFloat() <= right.asDouble());
      } else if (right.isFloat()) {
        return ValueFactory.createValue(left.asDouble() <= right.asFloat());
      } else if (left.isLong()) {
        return ValueFactory.createValue(left.asLong() <= right.asDouble());
      } else if (right.isLong()) {
        return ValueFactory.createValue(left.asDouble() <= right.asLong());
      }
    } else if (left.isFloat() || right.isFloat()) {
      if (left.isFloat() && right.isFloat()) {
        return ValueFactory.createValue(left.asFloat() <= right.asFloat());
      } else if (left.isChar()) {
        return ValueFactory.createValue(left.asChar() <= right.asFloat());
      } else if (right.isChar()) {
        return ValueFactory.createValue(left.asFloat() <= right.asChar());
      }
    } else if (left.isChar() && right.isChar()) {
      return ValueFactory.createValue(left.asChar() <= right.asChar());
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
      return ValueFactory.createValue(~res.asChar());
    } else if (res.isInt()) {
      return ValueFactory.createValue(~res.asInt());
    } else if (res.isLong()) {
      return ValueFactory.createValue(~res.asLong());
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
      return ValueFactory.createValue(!res.asBoolean());
    } else {
      Log.error("Logical Not operation is not applicable for these types.");
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTBooleanAndOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return ValueFactory
          .createValue
              (left.asBoolean() && right.asBoolean());
    } else {
      Log.error("Logical And operation is not applicable.");
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTBooleanOrOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());

    if (left.isBoolean() && right.isBoolean()) {
      return ValueFactory
          .createValue
              (left.asBoolean() || right.asBoolean());
    } else {
      Log.error("Logical Or operation is not applicable.");
    }
    return new NotAValue();
  }

  /*=================================================================*
  //Bracket operation
  /*=================================================================*/
  @Override
  public Value interpret(ASTBracketExpression node) {
    Value res = node.getExpression().evaluate(getRealThis());

    if (res.isInt()) {
      return ValueFactory.createValue(res.asInt());
    } else if (res.isDouble()) {
      return ValueFactory.createValue(res.asDouble());
    } else if (res.isChar()) {
      return ValueFactory.createValue(res.asChar());
    } else if (res.isString()) {
      return ValueFactory.createValue(res.asString());
    } else if (res.isObject()) {
      return ValueFactory.createValue(res.asObject());
    } else if (res.isFloat()) {
      return ValueFactory.createValue(res.asFloat());
    } else if (res.isLong()) {
      return ValueFactory.createValue(res.asLong());
    } else {
      return ValueFactory.createValue(res.asBoolean());
    }
  }


  /*=================================================================*/
  //Field Access operation
  /*=================================================================*/
  @Override
  public Value interpret(ASTFieldAccessExpression node) {
    String expression = CommonExpressionsMill.prettyPrint(node, false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveVariable(expression);
    if (symbol.isPresent()) {
      return load(symbol.get());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTLiteralExpression node) {
    return node.getLiteral().evaluate(getRealThis());
  }

}
