/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import static de.monticore.interpreter.MIValueFactory.createValue;

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
  
  public MIValue isEqual(SymTypePrimitive leftType, MIValue left, SymTypePrimitive rightType, MIValue right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "Equality operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
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
    return new ErrorMIValue(errorMsg);
  }
  
  public MIValue subtract(SymTypePrimitive leftType, MIValue left, SymTypePrimitive rightType, MIValue right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "Greater or Lesser operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
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
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTPlusExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, Integer::sum, Long::sum, Float::sum, Double::sum, "Plus");
  }

  @Override
  public MIValue interpret(ASTMinusExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a - b, (a, b) -> a - b,
        (a, b) -> a - b, (a, b) -> a - b, "Minus");
  }

  @Override
  public MIValue interpret(ASTMultExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a * b, (a, b) -> a * b,
        (a, b) -> a * b, (a, b) -> a * b, "Multiplication");
  }

  @Override
  public MIValue interpret(ASTDivideExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String resultPrimitive = type.asPrimitive().getPrimitiveName();
      
      if (right.asDouble() == 0.0) {
        String errorMsg = "Division by zero is undefined";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }
      
      return InterpreterUtils.calcOpPrimitive(left, right, resultPrimitive,
          (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b,
          "Division");
    }
    
    String errorMsg = "Division operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTModuloExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a % b, (a, b) -> a % b,
        (a, b) -> a % b, (a, b) -> a % b, "Modulo");
  }

  @Override
  public MIValue interpret(ASTMinusPrefixExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
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
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTPlusPrefixExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive() && (type.asPrimitive().isNumericType() || type.asPrimitive().isIntegralType())) {
      return value;
    }
    
    String errorMsg = "Minus Prefix operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTEqualsExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
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
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTNotEqualsExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = isEqual(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(!result.asBoolean());
    }
    
    String errorMsg = "Inequality operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTGreaterThanExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() > 0.0);
    }
    
    String errorMsg = "Greater than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTLessThanExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() < 0.0);
    }
    
    String errorMsg = "Less than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTGreaterEqualExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() >= 0.0);
    }
    
    String errorMsg = "Greater equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTLessEqualExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isError()) return left;
    if (right.isError()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isError()) return result;
      
      return createValue(result.asDouble() <= 0.0);
    }
    
    String errorMsg = "Less equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  //~ -> behaves as a bitwise complement
  @Override
  public MIValue interpret(ASTBooleanNotExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
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
    
    String errorMsg = "Bitwise Not operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /*=================================================================*/
  //Logical and boolean operations
  /*=================================================================*/

  @Override
  public MIValue interpret(ASTLogicalNotExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());
    
    if (value.isBoolean()) {
      return createValue(!value.asBoolean());
    }
    
    String errorMsg = "Logical Not operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBooleanAndOpExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
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
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBooleanOrOpExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
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
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBracketExpression node) {
    return node.getExpression().evaluate(getRealThis());
  }

  @Override
  public MIValue interpret(ASTConditionalExpression node) {
    MIValue condition = node.getCondition().evaluate(getRealThis());
    if (condition.isError()) return condition;
    
    return condition.asBoolean()
        ? node.getTrueExpression().evaluate(getRealThis())
        : node.getFalseExpression().evaluate(getRealThis());
  }

  @Override
  public MIValue interpret(ASTFieldAccessExpression node) {
    String errorMsg = "Field Access operation not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
//    String expression = CommonExpressionsMill.prettyPrint(node, false);
//    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveVariable(expression);
//    return symbol.map(this::load).orElse(new NullValue());
  }

  @Override
  public MIValue interpret(ASTLiteralExpression node) {
    return node.getLiteral().evaluate(getRealThis());
  }
  
//  @Override
//  public MIValue interpret(ASTCallExpression node) {
//    // evaluate expression that gives lambda/function
//    // get original parent scope of lambda/function declaration
//    // create Scope with parent and arguments
//    // evaluate arguments in current scope & put into new scope
//
//    // node.getExpression();
//    // parent = whatever
//
////    MIScope scope = new MIScope(parent);
////    List<ISymbol> parameterSymbols =
////    List<ASTExpression> arguments = node.getArguments().getExpressionList();
////    for (int i = 0; i < arguments.getSize(); i++) {
////      scope.declareVariable()
////    }
//  }
}