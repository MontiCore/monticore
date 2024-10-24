/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;

public class AssignmentExpressionsInterpreter extends AssignmentExpressionsInterpreterTOP {

  public AssignmentExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  public AssignmentExpressionsInterpreter() {
    super();
  }

  //i++
  @Override
  public Value interpret(ASTIncSuffixExpression n) {
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(), false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(expr);
    if (symbol.isPresent()) {
      Value value = load(symbol.get());
      if (value.isInt()) {
        Value res = ValueFactory.createValue(value.asInt() + 1);
        store(symbol.get(), res);
        return res;
      } else if (value.isChar()) {
        Value res = ValueFactory.createValue(value.asChar() + 1);
        store(symbol.get(), res);
        return res;
      } else if (value.isLong()) {
        Value res = ValueFactory.createValue(value.asLong() + 1);
        store(symbol.get(), res);
        return res;
      } else if (value.isFloat()) {
        Value res = ValueFactory.createValue(value.asFloat() + 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isDouble()) {
        Value res = ValueFactory.createValue(value.asDouble() + 1);
        store(symbol.get(), res);
        return (res);
      } else {
        Log.error("Suffix incrementation operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  //++i
  @Override
  public Value interpret(ASTIncPrefixExpression n) {
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(), false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(expr);
    if (symbol.isPresent()) {
      Value value = load(symbol.get());
      if (value.isInt()) {
        Value res = ValueFactory.createValue(value.asInt() + 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isChar()) {
        Value res = ValueFactory.createValue(value.asChar() + 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isLong()) {
        Value res = ValueFactory.createValue(value.asLong() + 1);
        store(symbol.get(), res);
        return (res);

      } else if (value.isFloat()) {
        Value res = ValueFactory.createValue(value.asFloat() + 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isDouble()) {
        Value res = ValueFactory.createValue(value.asDouble() + 1);
        store(symbol.get(), res);
        return (res);
      } else {
        Log.error("Prefix incrementation operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  //i--
  @Override
  public Value interpret(ASTDecSuffixExpression n) {
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(), false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(expr);
    if (symbol.isPresent()) {
      Value value = load(symbol.get());
      if (value.isInt()) {
        Value res = ValueFactory.createValue(value.asInt() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isChar()) {
        Value res = ValueFactory.createValue(value.asChar() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isLong()) {
        Value res = ValueFactory.createValue(value.asLong() - 1);
        store(symbol.get(), res);
        return (res);

      } else if (value.isFloat()) {
        Value res = ValueFactory.createValue(value.asFloat() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isDouble()) {
        Value res = ValueFactory.createValue(value.asDouble() - 1);
        store(symbol.get(), res);
        return (res);
      } else {
        Log.error("Suffix decremental operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  //--i
  @Override
  public Value interpret(ASTDecPrefixExpression n) {
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(), false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(expr);
    if (symbol.isPresent()) {
      Value value = load(symbol.get());
      if (value.isInt()) {
        Value res = ValueFactory.createValue(value.asInt() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isChar()) {
        Value res = ValueFactory.createValue(value.asChar() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isLong()) {
        Value res = ValueFactory.createValue(value.asLong() - 1);
        store(symbol.get(), res);
        return (res);

      } else if (value.isFloat()) {
        Value res = ValueFactory.createValue(value.asFloat() - 1);
        store(symbol.get(), res);
        return (res);
      } else if (value.isDouble()) {
        Value res = ValueFactory.createValue(value.asDouble() - 1);
        store(symbol.get(), res);
        return (res);
      } else {
        Log.error("Prefix decremental operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTAssignmentExpression n) {
    String leftExpression = AssignmentExpressionsMill.prettyPrint(n.getLeft(), false);
    Optional<VariableSymbol> leftSymbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(leftExpression);

    if (leftSymbol.isPresent()) {
      Value rightValue = n.getRight().evaluate(getRealThis());
      int operator = n.getOperator();

      switch (operator) {
        case AND_EQUALS: { //bitwise and
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error("&= operation is not suitable for these types.");
          } else if (!(leftValue.isInt() || leftValue.isLong() || leftValue.isChar())) {
            Log.error("&= operation is not suitable for these types.");
          }

          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() & rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() & rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() & rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }

          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() & rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() & rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() & rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() & rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() & rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() & rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case EQUALS: {
          VariableSymbol symbol = leftSymbol.get();

          if (SymTypeRelations.isString(symbol.getType()) && rightValue.isString()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isBoolean(symbol.getType()) && rightValue.isBoolean()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isChar(symbol.getType()) && rightValue.isChar()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isInt(symbol.getType()) && rightValue.isInt()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isLong(symbol.getType()) && rightValue.isLong()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isFloat(symbol.getType()) && rightValue.isFloat()) {
            store(leftSymbol.get(), rightValue);
          } else if (SymTypeRelations.isDouble(symbol.getType()) && rightValue.isDouble()) {
            store(leftSymbol.get(), rightValue);
          } else if (rightValue.isObject()) {
            store(leftSymbol.get(), rightValue);
          } else {
            Log.error("The interpreter only allows = operation for operands of the same type.");
          }
          break;
        }

        case GTGTEQUALS: { //bitwise rightValue shift
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error(">>= operation is not suitable for these types.");
          } else if (!(leftValue.isChar() || leftValue.isInt() || leftValue.isLong())) {
            Log.error(">>= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case GTGTGTEQUALS: { //bitwise rightValue shift
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error(">>>= operation is not suitable for these types.");
          } else if (!(leftValue.isInt() || leftValue.isLong() || leftValue.isChar())) {
            Log.error(">>>= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >>> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >>> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() >>> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >>> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >>> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() >>> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >>> rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >>> rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() >>> rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case LTLTEQUALS: {
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error("<<= operation is not suitable for these types.");
          } else if (!(leftValue.isInt() || leftValue.isLong() || leftValue.isChar())) {
            Log.error("<<= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() << rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() << rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() << rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() << rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() << rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() << rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() << rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() << rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() << rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case MINUSEQUALS: {
          Value leftValue = load(leftSymbol.get());
          if (rightValue.isObject() || rightValue.isString() || rightValue.isBoolean()) {
            Log.error("-= operation is not suitable for these types.");
          } else if (leftValue.isObject() || leftValue.isString() || leftValue.isBoolean()) {
            Log.error("-= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isFloat()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isDouble()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case PERCENTEQUALS: {
          Value leftValue = load(leftSymbol.get());
          if (rightValue.isObject() || rightValue.isString() || rightValue.isBoolean()) {
            Log.error("%= operation is not suitable for these types.");
          } else if (leftValue.isObject() || leftValue.isString() || leftValue.isBoolean()) {
            Log.error("%= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isFloat()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isDouble()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case PIPEEQUALS: {
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error("|= operation is not suitable for these types.");
          } else if (!(leftValue.isChar() || leftValue.isInt() || leftValue.isLong())) {
            Log.error("|= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() | rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() | rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() | rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() | rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() | rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() | rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() | rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() | rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() | rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case PLUSEQUALS: {
          Value leftValue = load(leftSymbol.get());

          if (leftValue.isString()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asBoolean());
              store(leftSymbol.get(), res);
              return (res);
            }
          } else if (leftValue.isBoolean() || leftValue.isObject() || rightValue.isObject()) {
            Log.error("+= operation is not suitable for these types.");
          } else if (leftValue.isChar()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Log.error("+= operation is not suitable for these types.");
            }
          } else if (leftValue.isInt()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Log.error("+= operation is not suitable for these types.");
            }
          } else if (leftValue.isLong()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Log.error("+= operation is not suitable for these types.");
            }
          } else if (leftValue.isFloat()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Log.error("+= operation is not suitable for these types.");
            }
          } else if (leftValue.isDouble()) {
            if (rightValue.isString()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asString());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            } else if (rightValue.isBoolean()) {
              Log.error("+= operation is not suitable for these types.");
            }
          }
          break;
        }

        case ROOFEQUALS: { //XOR
          Value leftValue = load(leftSymbol.get());
          if (!(rightValue.isInt() || rightValue.isLong() || rightValue.isChar())) {
            Log.error("^= operation is not suitable for these types.");
          } else if (!(leftValue.isChar() || leftValue.isInt() || leftValue.isLong())) {
            Log.error("^= operation is not suitable for these types.");
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() ^ rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() ^ rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() ^ rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() ^ rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() ^ rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() ^ rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() ^ rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() ^ rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() ^ rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case SLASHEQUALS: {
          Value leftValue = load(leftSymbol.get());
          if (rightValue.isObject() || rightValue.isString() || rightValue.isBoolean()) {
            Log.error("/= operation is not suitable for these types.");
          } else if (leftValue.isObject() || leftValue.isString() || leftValue.isBoolean()) {
            Log.error("/= operation is not suitable for these types.");
          } else if (leftValue.isLong()) {
            if (rightValue.asDouble() == 0) {
              Log.error("Division by 0 is not supported.");
            }
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          } else if (leftValue.isInt()) {
            if (rightValue.asDouble() == 0) {
              Log.error("Division by 0 is not supported.");
            }
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          } else if (leftValue.isChar()) {
            if (rightValue.asDouble() == 0) {
              Log.error("Division by 0 is not supported.");
            }
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          } else if (leftValue.isFloat()) {
            if (rightValue.asDouble() == 0) {
              Log.error("Division by 0 is not supported.");
            }
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          } else if (leftValue.isDouble()) {
            if (rightValue.asDouble() == 0) {
              Log.error("Division by 0 is not supported.");
            }
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }

        case (STAREQUALS): {
          Value leftValue = load(leftSymbol.get());
          if (rightValue.isObject() || rightValue.isString() || rightValue.isBoolean()) {
            Log.error("*= operation is not suitable for these types.");
          } else if (leftValue.isObject() || leftValue.isString() || leftValue.isBoolean()) {
            Log.error("*= operation is not suitable for these types.");
          }
          if (leftValue.isLong()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isInt()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isChar()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isFloat()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          if (leftValue.isDouble()) {
            if (rightValue.isInt()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asInt());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isLong()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asLong());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isFloat()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asFloat());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isDouble()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asDouble());
              store(leftSymbol.get(), res);
              return (res);
            }
            if (rightValue.isChar()) {
              Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asChar());
              store(leftSymbol.get(), res);
              return (res);
            }
          }
          break;
        }
        default:
          Log.error("Operator is not defined.");
      }
    }
    return new NotAValue();
  }
}
