/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;
import static de.monticore.interpreter.MIValueFactory.createValue;

public class AssignmentExpressionsInterpreter extends AssignmentExpressionsInterpreterTOP {

  public AssignmentExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  public AssignmentExpressionsInterpreter() {
    super();
  }

  //i++
  @Override
  public MIValue interpret(ASTIncSuffixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    Optional<ISymbol> symbol = type.getSourceInfo().getSourceSymbol();
    if (symbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    MIValue value = load(symbol.get());
    if (value.isError()) return value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() + 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() + 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() + 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() + 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() + 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() + 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() + 1);
        store(symbol.get(), res);
        return value;
      }
    }
    String errorMsg = "Suffix incrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  //++i
  @Override
  public MIValue interpret(ASTIncPrefixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    Optional<ISymbol> symbol = type.getSourceInfo().getSourceSymbol();
    if (symbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    MIValue value = load(symbol.get());
    if (value.isError()) return value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() + 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() + 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() + 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() + 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() + 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() + 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() + 1);
        store(symbol.get(), res);
        return res;
      }
    }
    String errorMsg = "Prefix incrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  //i--
  @Override
  public MIValue interpret(ASTDecSuffixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    Optional<ISymbol> symbol = type.getSourceInfo().getSourceSymbol();
    if (symbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    MIValue value = load(symbol.get());
    if (value.isError()) return value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() - 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() - 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() - 1));
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() - 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() - 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() - 1);
        store(symbol.get(), res);
        return value;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() - 1);
        store(symbol.get(), res);
        return value;
      }
    }
    String errorMsg = "Suffix decrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  //--i
  @Override
  public MIValue interpret(ASTDecPrefixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    Optional<ISymbol> symbol = type.getSourceInfo().getSourceSymbol();
    if (symbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    MIValue value = load(symbol.get());
    if (value.isError()) return value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() - 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() - 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() - 1));
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() - 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() - 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() - 1);
        store(symbol.get(), res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() - 1);
        store(symbol.get(), res);
        return res;
      }
    }
    String errorMsg = "Prefix decrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTAssignmentExpression n) {
    ASTExpression leftExpr = n.getLeft();
    SymTypeExpression leftType = TypeCheck3.typeOf(leftExpr);
    Optional<ISymbol> leftSymbol = leftType.getSourceInfo().getSourceSymbol();
    if (leftSymbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    int operator = n.getOperator();

    MIValue rightValue = n.getRight().evaluate(getRealThis());
    if (rightValue.isError()) return rightValue;
    
    SymTypeExpression rightType = TypeCheck3.typeOf(n.getRight());
    
    // no operation
    if (operator == EQUALS) {
      if (leftType.deepEquals(rightType)) {
        store(leftSymbol.get(), rightValue);
        return rightValue;
      }
      
      if (!SymTypeRelations.isCompatible(leftType, rightType)) {
        String errorMsg = "A value of type " + rightType.print() + " can not be writen to a variable of type " + leftType.print() + ".";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }
      
      if (leftType.isPrimitive() && rightType.isPrimitive()) {
        rightValue = InterpreterUtils.convertToPrimitiveImplicit(leftType.asPrimitive().getPrimitiveName(), rightValue);
      } else {
        String errorMsg = "The implicit conversion from " + rightType.print() + " to " + leftType.print() + " is not supported.";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }
      
      store(leftSymbol.get(), rightValue);
      return rightValue;
    }
    
    MIValue leftValue = load(leftSymbol.get());
    if (leftValue.isError()) return leftValue;
    
    MIValue resultValue;
    SymTypeExpression resultType;
    
    switch (operator) {
      case AND_EQUALS: { //bitwise and
        resultType = TypeVisitorOperatorCalculator.binaryAnd(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
            (a, b) -> a & b, (a, b) -> a & b, (a, b) -> a & b, "Bitwise And Assignment");
        break;
      }

      case GTGTEQUALS: { //bitwise rightValue shift
        resultType = TypeVisitorOperatorCalculator.signedRightShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a >> b, (a, b) -> a >> b, "Bitwise Right Shift Assignment");
        break;
      }

      case GTGTGTEQUALS: { //bitwise rightValue shift
        resultType = TypeVisitorOperatorCalculator.unsignedRightShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a >>> b, (a, b) -> a >>> b, "Logical Right Shift Assignment");
        break;
      }

      case LTLTEQUALS: {
        resultType = TypeVisitorOperatorCalculator.leftShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a << b, (a, b) -> a << b, "Bitwise Left Shift Assignment");
        break;
      }

      case MINUSEQUALS: {
        resultType = TypeVisitorOperatorCalculator.minus(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a - b, (a, b) -> a - b, (a, b) -> a - b, (a, b) -> a - b,
            "Minus Assignment");
        break;
      }

      case PERCENTEQUALS: {
        resultType = TypeVisitorOperatorCalculator.modulo(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a % b, (a, b) -> a % b, (a, b) -> a % b, (a, b) -> a % b,
            "Modulo Assignment");
        break;
      }

      case PIPEEQUALS: {
        resultType = TypeVisitorOperatorCalculator.binaryOr(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
            (a, b) -> a | b, (a, b) -> a | b, (a, b) -> a | b, "Bitwise Or Assignment");
        break;
      }

      case PLUSEQUALS: {
        resultType = TypeVisitorOperatorCalculator.plus(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            Integer::sum, Long::sum, Float::sum, Double::sum,
              "Plus Assignment");
        break;
      }

      case ROOFEQUALS: { //XOR
        resultType = TypeVisitorOperatorCalculator.binaryXor(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
              (a, b) -> a ^ b, (a, b) -> a ^ b, (a, b) -> a ^ b, "Bitwise Xor Assignment");
        break;
      }

      case SLASHEQUALS: {
        resultType = TypeVisitorOperatorCalculator.divide(leftType, rightType).get();
        if (resultType.isPrimitive()) {
          String resultPrimitive = resultType.asPrimitive().getPrimitiveName();
          
          if (rightValue.asDouble() == 0.0) {
            String errorMsg = "Division by zero is undefined";
            Log.error(errorMsg);
            return new ErrorMIValue(errorMsg);
          }
          
          resultValue = InterpreterUtils.calcOpPrimitive(leftValue, rightValue, resultPrimitive,
              (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b,
              "Division Assignment");
          break;
        }
        String errorMsg = "Division Assignment operation with result of type " + resultType + " is not supported.";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }

      case STAREQUALS: {
        resultType = TypeVisitorOperatorCalculator.multiply(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a * b, (a, b) -> a * b, (a, b) -> a * b, (a, b) -> a * b,
            "Multiplication Assignment");
        break;
      }
      default:
        Log.error("Operator is not defined.");
        return new ErrorMIValue("Operator is not defined.");
    }
    
    if (resultValue.isError()) return resultValue;
    
    if (leftType.deepEquals(resultType)) {
    } else if (leftType.isPrimitive() && resultType.isPrimitive()) {
      resultValue = InterpreterUtils.convertToPrimitiveExplicit(resultType.asPrimitive().getPrimitiveName(),
          leftType.asPrimitive().getPrimitiveName(), resultValue);
    } else {
      String errorMsg = "Cast from " + resultType.print() + " to " + leftType.print() + " is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    if (resultValue.isError()) return resultValue;
    
    store(leftSymbol.get(), resultValue);
    return resultValue;
  }
}
