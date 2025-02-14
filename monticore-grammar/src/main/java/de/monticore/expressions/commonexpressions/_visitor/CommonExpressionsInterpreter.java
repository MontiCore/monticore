/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.values.ErrorValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import static de.monticore.interpreter.ValueFactory.createValue;

public class CommonExpressionsInterpreter extends CommonExpressionsInterpreterTOP {

  public CommonExpressionsInterpreter() {
    super();
  }

  public CommonExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  public SymTypePrimitive getCompatibleType(SymTypePrimitive type1, SymTypePrimitive type2) {
    return SymTypeRelations.isCompatible(type1, type2)
      ? type1
      : (
        SymTypeRelations.isCompatible(type2, type1)
          ? type2
          : null
    );
  }
  
  public Value isEqual(SymTypePrimitive leftType, Value left, SymTypePrimitive rightType, Value right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "Equality operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorValue(errorMsg);
    }
    
    String primitive = compatibleType.getPrimitiveName();
    if (primitive.equals(BasicSymbolsMill.BOOLEAN)) {
      return createValue(left.asBoolean() == right.asBoolean());
    } else if (primitive.equals(BasicSymbolsMill.BYTE)) {
      return createValue(left.asByte() == right.asByte());
    } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
      return createValue(left.asShort() == right.asShort());
    } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
      return createValue(left.asChar() == right.asChar());
    } else if (primitive.equals(BasicSymbolsMill.INT)) {
      return createValue(left.asInt() == right.asInt());
    } else if (primitive.equals(BasicSymbolsMill.LONG)) {
      return createValue(left.asLong() == right.asLong());
    } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(left.asFloat() == right.asFloat());
    } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(left.asDouble() == right.asDouble());
    }
    
    String errorMsg = "Equality operator with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not implemented.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }
  
  public Value subtract(SymTypePrimitive leftType, Value left, SymTypePrimitive rightType, Value right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "Greater or Lesser operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorValue(errorMsg);
    }
    
    String primitive = compatibleType.getPrimitiveName();
    if (primitive.equals(BasicSymbolsMill.BYTE)) {
      return createValue(left.asByte() - right.asByte());
    } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
      return createValue(left.asShort() - right.asShort());
    } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
      return createValue(left.asChar() - right.asChar());
    } else if (primitive.equals(BasicSymbolsMill.INT)) {
      return createValue(left.asInt() - right.asInt());
    } else if (primitive.equals(BasicSymbolsMill.LONG)) {
      return createValue(left.asLong() - right.asLong());
    } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(left.asFloat() - right.asFloat());
    } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(left.asDouble() - right.asDouble());
    }
    
    String errorMsg = "Greater or Lesser operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTPlusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(left.asByte() + right.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(left.asShort() + right.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(left.asChar() + right.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(left.asInt() + right.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(left.asLong() + right.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(left.asFloat() + right.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(left.asDouble() + right.asDouble());
      }
    }
    
    String errorMsg = "Plus operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTMinusExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression typeLeft = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression typeRight = TypeCheck3.typeOf(node.getRight());
    if (typeLeft.isPrimitive() && typeRight.isPrimitive()) {
      return subtract(typeLeft.asPrimitive(), left, typeRight.asPrimitive(), right);
    }
    
    String errorMsg = "Minus operation with operands of type '" + typeLeft.print() + "' and '"
        + typeRight.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTMultExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(left.asByte() * right.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(left.asShort() * right.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(left.asChar() * right.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(left.asInt() * right.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(left.asLong() * right.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(left.asFloat() * right.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(left.asDouble() * right.asDouble());
      }
    }
    
    String errorMsg = "Multiplication operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTDivideExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive() && (type.asPrimitive().isIntegralType() || type.asPrimitive().isNumericType())) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (right.asDouble() == 0.0) {
        String errorMsg = "Division by zero is undefined";
        Log.error(errorMsg);
        return new ErrorValue(errorMsg);
      }
      
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(left.asByte() * right.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(left.asShort() * right.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(left.asChar() * right.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(left.asInt() * right.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(left.asLong() * right.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(left.asFloat() * right.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(left.asDouble() * right.asDouble());
      }
    }
    
    String errorMsg = "Division operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTModuloExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(left.asByte() % right.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(left.asShort() % right.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(left.asChar() % right.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(left.asInt() % right.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(left.asLong() % right.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(left.asFloat() % right.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(left.asDouble() % right.asDouble());
      }
    }
    
    String errorMsg = "Modulo operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTMinusPrefixExpression node) {
    Value value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(-value.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(-value.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(-value.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(-value.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(-value.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(-value.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(-value.asDouble());
      }
    }
    
    String errorMsg = "Minus Prefix operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTPlusPrefixExpression node) {
    Value value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive() && (type.asPrimitive().isNumericType() || type.asPrimitive().isIntegralType())) {
      return value;
    }
    
    String errorMsg = "Minus Prefix operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      return isEqual(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
    }
    
    String errorMsg = "Equality operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTNotEqualsExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      Value result = isEqual(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(!result.asBoolean());
    }
    
    String errorMsg = "Inequality operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTGreaterThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      Value result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() > 0.0);
    }
    
    String errorMsg = "Greater than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTLessThanExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      Value result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() < 0.0);
    }
    
    String errorMsg = "Less than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTGreaterEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      Value result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() >= 0.0);
    }
    
    String errorMsg = "Greater equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTLessEqualExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      Value result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() <= 0.0);
    }
    
    String errorMsg = "Less equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  //~ -> behaves as a bitwise complement
  @Override
  public Value interpret(ASTBooleanNotExpression node) {
    Value value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;

    SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());
    if (type.isPrimitive() && type.asPrimitive().isIntegralType()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(~value.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(~value.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(~value.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(~value.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(~value.asLong());
      }
    }
    
    String errorMsg = "Bitwise Not opeartion with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  /*=================================================================*/
  //Logical and boolean operations
  /*=================================================================*/

  @Override
  public Value interpret(ASTLogicalNotExpression node) {
    Value value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());
    
    if (value.isBoolean()) {
      return createValue(!value.asBoolean());
    }
    
    String errorMsg = "Logical Not operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTBooleanAndOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() && right.asBoolean());
    }
    
    String errorMsg = "Logical And operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTBooleanOrOpExpression node) {
    Value left = node.getLeft().evaluate(getRealThis());
    Value right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    
    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() || right.asBoolean());
    }
    
    String errorMsg = "Logical Or operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTBracketExpression node) {
    return node.getExpression().evaluate(getRealThis());
  }

  @Override
  public Value interpret(ASTConditionalExpression node) {
    Value condition = node.getCondition().evaluate(getRealThis());
    if (condition.isError()) return condition;
    
    return condition.asBoolean()
        ? node.getTrueExpression().evaluate(getRealThis())
        : node.getFalseExpression().evaluate(getRealThis());
  }

  @Override
  public Value interpret(ASTFieldAccessExpression node) {
    String errorMsg = "Field Access operation not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
//    String expression = CommonExpressionsMill.prettyPrint(node, false);
//    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveVariable(expression);
//    return symbol.map(this::load).orElse(new NullValue());
  }

  @Override
  public Value interpret(ASTLiteralExpression node) {
    return node.getLiteral().evaluate(getRealThis());
  }
}