/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.WriteableMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;
import static de.monticore.interpreter.MIValueFactory.createValue;

public class AssignmentExpressionsInterpreter extends AssignmentExpressionsInterpreterTOP {

  public AssignmentExpressionsInterpreter(IModelInterpreter realThis) {
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
    MIValue value = n.getExpression().evaluate(getRealThis());
    
    if (value.isFlowControlSignal()) return value;
    if (!value.isWriteable()) {
      String errorMsg = "0x57094 Expected an LValue. Got " + value.printType() + " (" + value.printValue() + ").";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    WriteableMIValue variable = (WriteableMIValue)value;
    
    if (type.isPrimitive()) {
      MIValue previousValue = variable.getMIValue();
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(variable.asByte() + 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(variable.asShort() + 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(variable.asChar() + 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(variable.asInt() + 1);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(variable.asLong() + 1L);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(variable.asFloat() + 1.0f);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(variable.asDouble() + 1.0);
        variable.write(res);
        return previousValue;
      }
    }
    String errorMsg = "0x57023 Suffix incrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  //++i
  @Override
  public MIValue interpret(ASTIncPrefixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    MIValue value = n.getExpression().evaluate(getRealThis());
    
    if (value.isFlowControlSignal()) return value;
    if (!value.isWriteable()) {
      String errorMsg = "0x57095 Expected an LValue. Got " + value.printType() + " (" + value.printValue() + ").";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    WriteableMIValue variable = (WriteableMIValue)value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() + 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() + 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() + 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() + 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() + 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() + 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() + 1);
        variable.write(res);
        return res;
      }
    }
    String errorMsg = "0x57025 Prefix incrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  //i--
  @Override
  public MIValue interpret(ASTDecSuffixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    MIValue value = n.getExpression().evaluate(getRealThis());
    
    if (value.isFlowControlSignal()) return value;
    if (!value.isWriteable()) {
      String errorMsg = "0x57096 Expected an LValue. Got " + value.printType() + " (" + value.printValue() + ").";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    WriteableMIValue variable = (WriteableMIValue)value;
    if (type.isPrimitive()) {
      MIValue previousValue = variable.getMIValue();
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() - 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() - 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() - 1));
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() - 1);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() - 1);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() - 1);
        variable.write(res);
        return previousValue;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() - 1);
        variable.write(res);
        return previousValue;
      }
    }
    String errorMsg = "0x57027 Suffix decrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  //--i
  @Override
  public MIValue interpret(ASTDecPrefixExpression n) {
    ASTExpression expr = n.getExpression();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    MIValue value = n.getExpression().evaluate(getRealThis());
    
    if (value.isFlowControlSignal()) return value;
    if (!value.isWriteable()) {
      String errorMsg = "0x57097 Expected an LValue. Got " + value.printType() + " (" + value.printValue() + ").";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    WriteableMIValue variable = (WriteableMIValue)value;
    
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        MIValue res = createValue((byte)(value.asByte() - 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        MIValue res = createValue((short)(value.asShort() - 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        MIValue res = createValue((char)(value.asChar() - 1));
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        MIValue res = createValue(value.asInt() - 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        MIValue res = createValue(value.asLong() - 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        MIValue res = createValue(value.asFloat() - 1);
        variable.write(res);
        return res;
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        MIValue res = createValue(value.asDouble() - 1);
        variable.write(res);
        return res;
      }
    }
    String errorMsg = "0x57029 Prefix decrementation operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTAssignmentExpression n) {
    ASTExpression leftExpr = n.getLeft();
    SymTypeExpression leftType = TypeCheck3.typeOf(leftExpr);
    MIValue leftValue = n.getLeft().evaluate(getRealThis());
    
    if (!leftValue.isWriteable()) {
      String errorMsg = "0x57098 Expected an LValue. Got " + leftValue.printType()
          + " (" + leftValue.printValue() + ").";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    WriteableMIValue variable = (WriteableMIValue)leftValue;
    
    int operator = n.getOperator();

    MIValue rightValue = n.getRight().evaluate(getRealThis());
    if (rightValue.isFlowControlSignal()) return rightValue;
    
    SymTypeExpression rightType = TypeCheck3.typeOf(n.getRight());
    
    // normal assignment without operation
    if (operator == EQUALS) {
      if (leftType.deepEquals(rightType)) {
        variable.write(rightValue);
        return rightValue;
      }
      
      if (!SymTypeRelations.isCompatible(leftType, rightType)) {
        String errorMsg = "0x57031 A value of type " + rightType.print() + " can not be writen to a variable of type " + leftType.print() + ".";
        Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      
      if (leftType.isPrimitive() && rightType.isPrimitive()) {
        rightValue = InterpreterUtils.convertToPrimitiveImplicit(leftType.asPrimitive().getPrimitiveName(), rightValue);
      } else {
        String errorMsg = "0x57032 The implicit conversion from " + rightType.print() + " to " + leftType.print() + " is not supported.";
        Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      
      variable.write(rightValue);
      return rightValue;
    }
    
    if (leftValue.isFlowControlSignal()) return leftValue;
    
    MIValue resultValue;
    SymTypeExpression resultType;
    
    switch (operator) {
      case AND_EQUALS: { //bitwise and
        resultType = TypeVisitorOperatorCalculator.binaryAnd(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
            (a, b) -> a & b, (a, b) -> a & b, (a, b) -> a & b,
            "0x57042 Bitwise And Assignment");
        break;
      }

      case GTGTEQUALS: { //bitwise rightValue shift
        resultType = TypeVisitorOperatorCalculator.signedRightShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a >> b, (a, b) -> a >> b,
            "0x57043 Bitwise Right Shift Assignment");
        break;
      }

      case GTGTGTEQUALS: { //bitwise rightValue shift
        resultType = TypeVisitorOperatorCalculator.unsignedRightShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a >>> b, (a, b) -> a >>> b,
            "0x57044 Logical Right Shift Assignment");
        break;
      }

      case LTLTEQUALS: {
        resultType = TypeVisitorOperatorCalculator.leftShift(leftType, rightType).get();
        resultValue = InterpreterUtils.calcShift(leftValue, rightValue, resultType,
            (a, b) -> a << b, (a, b) -> a << b,
            "0x57045 Bitwise Left Shift Assignment");
        break;
      }

      case MINUSEQUALS: {
        resultType = TypeVisitorOperatorCalculator.minus(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a - b, (a, b) -> a - b, (a, b) -> a - b, (a, b) -> a - b,
            "0x57046 Minus Assignment");
        break;
      }

      case PERCENTEQUALS: {
        resultType = TypeVisitorOperatorCalculator.modulo(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a % b, (a, b) -> a % b, (a, b) -> a % b, (a, b) -> a % b,
            "0x57047 Modulo Assignment");
        break;
      }

      case PIPEEQUALS: {
        resultType = TypeVisitorOperatorCalculator.binaryOr(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
            (a, b) -> a | b, (a, b) -> a | b, (a, b) -> a | b,
            "0x57048 Bitwise Or Assignment");
        break;
      }

      case PLUSEQUALS: {
        resultType = TypeVisitorOperatorCalculator.plus(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            Integer::sum, Long::sum, Float::sum, Double::sum,
            "0x57049 Plus Assignment");
        break;
      }

      case ROOFEQUALS: { //XOR
        resultType = TypeVisitorOperatorCalculator.binaryXor(leftType, rightType).get();
        resultValue = InterpreterUtils.calcBitwiseLogicalOp(leftValue, rightValue, resultType,
            (a, b) -> a ^ b, (a, b) -> a ^ b, (a, b) -> a ^ b,
            "0x57050 Bitwise Xor Assignment");
        break;
      }

      case SLASHEQUALS: {
        resultType = TypeVisitorOperatorCalculator.divide(leftType, rightType).get();
        if (resultType.isPrimitive()) {
          String resultPrimitive = resultType.asPrimitive().getPrimitiveName();
          
          if (rightValue.asDouble() == 0.0) {
            String errorMsg = "0x57033 Division by zero is undefined";
            Log.error(errorMsg, n.getRight().get_SourcePositionStart(), n.getRight().get_SourcePositionEnd());
            return new ErrorMIValue(errorMsg);
          }
          
          resultValue = InterpreterUtils.calcOpPrimitive(leftValue, rightValue, resultPrimitive,
              (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b,
              "0x57051 Division Assignment");
          break;
        }
        String errorMsg = "0x57034 Division Assignment operation with result of type " + resultType + " is not supported.";
        Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }

      case STAREQUALS: {
        resultType = TypeVisitorOperatorCalculator.multiply(leftType, rightType).get();
        resultValue = InterpreterUtils.calcOp(leftValue, rightValue, resultType,
            (a, b) -> a * b, (a, b) -> a * b, (a, b) -> a * b, (a, b) -> a * b,
            "0x57052 Multiplication Assignment");
        break;
      }
      default:
        String errorMsg = "0x57035 Operator is not defined";
        Log.error(errorMsg, n.getLeft().get_SourcePositionEnd(), n.getRight().get_SourcePositionStart());
        return new ErrorMIValue(errorMsg);
    }
    
    if (resultValue.isFlowControlSignal()) return resultValue;
    
    if (leftType.deepEquals(resultType)) {
      // nothing left to do
    } else if (leftType.isPrimitive() && resultType.isPrimitive()) {
      resultValue = InterpreterUtils.convertToPrimitiveExplicit(resultType.asPrimitive().getPrimitiveName(),
          leftType.asPrimitive().getPrimitiveName(), resultValue);
    } else {
      String errorMsg = "0x57036 Cast from " + resultType.print() + " to " + leftType.print() + " is not supported.";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    
    if (resultValue.isFlowControlSignal()) return resultValue;
    
    variable.write(resultValue);
    return resultValue;
  }
}
