// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsInheritanceHandler;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.setters.MISetterBoolean;
import de.monticore.interpreter.setters.MISetterDouble;
import de.monticore.interpreter.setters.MISetterInt;
import de.monticore.interpreter.setters.MISetterValue;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.util.InterpreterVisitorOperatorCalculator;
import de.monticore.interpreter.util.NativeStorageSelector;
import de.monticore.values.MCValue;
import de.monticore.types.check.SymTypeExpression;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsInt;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for AssigmentExpressions
 */
public class AssignmentExpressionsInterpreter
    extends AssignmentExpressionsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  protected InterpreterVisitorOperatorCalculator opCalculator =
      new InterpreterVisitorOperatorCalculator();

  public AssignmentExpressionsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTAssignmentExpression node) {
    node.getLeft().accept(getTraverser());
    MICalculation leftCalc = iData.popCalculation();
    MISetter setter = iData.popSetter();

    node.getRight().accept(getTraverser());
    MICalculation rightCalc = iData.popCalculation();

    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    SymTypeExpression exprType = normalize(typeOf(node));

    int operator = node.getOperator();
    MICalculation opCalc = switch (operator) {
      case ASTConstantsAssignmentExpressions.EQUALS -> rightCalc;
      case ASTConstantsAssignmentExpressions.PLUSEQUALS -> opCalculator
          .handlePlus(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.MINUSEQUALS -> opCalculator
          .handleMinus(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.STAREQUALS -> opCalculator
          .handleMultiply(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.SLASHEQUALS -> opCalculator
          .handleDivide(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.PERCENTEQUALS -> opCalculator
          .handleModulo(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.LTLTEQUALS -> opCalculator
          .handleLeftShift(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.GTGTEQUALS -> opCalculator
          .handleRightShift(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.GTGTGTEQUALS -> opCalculator
          .handleLogicalRightShift(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.AND_EQUALS -> opCalculator
          .handleBinaryAnd(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.ROOFEQUALS -> opCalculator
          .handleBinaryXor(leftCalc, rightCalc, leftType, rightType, exprType);
      case ASTConstantsAssignmentExpressions.PIPEEQUALS -> opCalculator
          .handleBinaryOr(leftCalc, rightCalc, leftType, rightType, exprType);
      default -> throw new NotImplementedException(
          "Unsupported assignment operator: " + operator
              + " at " + node.get_SourcePositionStart()
      );
    };

    MICalculation result = NativeStorageSelector.
        <Supplier<MICalculation>> switchByFormat(exprType,
        () -> {
          MICalculationBoolean opCalcBoolean = opCalc.asCalculationBoolean();
          MISetterBoolean setterBoolean = setter.asSetterBoolean();
          return (MICalculationBoolean) frame -> {
            boolean value = opCalcBoolean.calculate(frame);
            setterBoolean.set(frame, value);
            return value;
          };
        },
        () -> {
          MICalculationInt opCalcInt = opCalc.asCalculationInt();
          MISetterInt setterInt = setter.asSetterInt();
          return (MICalculationInt) frame -> {
            int value = opCalcInt.calculate(frame);
            setterInt.set(frame, value);
            return value;
          };
        },
        () -> {
          MICalculationDouble opCalcDouble = opCalc.asCalculationDouble();
          MISetterDouble setterDouble = setter.asSetterDouble();
          return (MICalculationDouble) frame -> {
            double value = opCalcDouble.calculate(frame);
            setterDouble.set(frame, value);
            return value;
          };
        },
        () -> {
          MICalculationValue opCalcValue = opCalc.asCalculationValue();
          MISetterValue setterValue = setter.asSetterValue();
          return (MICalculationValue) frame -> {
            MCValue value = opCalcValue.calculate(frame);
            setterValue.set(frame, value);
            return value;
          };
        }
    ).get();
    iData.putCalculation(result);
  }

  @Override
  public void traverse(ASTIncPrefixExpression node) {
    traverseIncDecPrefix(node, node.getExpression(), 1);
  }

  @Override
  public void traverse(ASTIncSuffixExpression node) {
    traverseIncDecSuffix(node, node.getExpression(), 1);
  }

  @Override
  public void traverse(ASTDecPrefixExpression node) {
    traverseIncDecPrefix(node, node.getExpression(), -1);
  }

  @Override
  public void traverse(ASTDecSuffixExpression node) {
    traverseIncDecSuffix(node, node.getExpression(), -1);
  }

  protected void traverseIncDecPrefix(
      ASTExpression expr,
      ASTExpression innerExpr,
      int valueToBeAdded
  ) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    innerExpr.accept(traverser);
    MICalculation innerCalc = iData.popCalculation();
    MISetter setter = iData.popSetter();
    MICalculation result;
    if (isStoredAsInt(exprType)) {
      MICalculationInt innerCalcInt = innerCalc.asCalculationInt();
      MISetterInt setterInt = setter.asSetterInt();
      result = (MICalculationInt) frame -> {
        int value = innerCalcInt.calculate(frame) + valueToBeAdded;
        setterInt.set(frame, value);
        return value;
      };
    }
    else {
      MICalculationDouble innerCalcDouble = innerCalc.asCalculationDouble();
      MISetterDouble setterDouble = setter.asSetterDouble();
      result = (MICalculationDouble) frame -> {
        double value = innerCalcDouble.calculate(frame) + valueToBeAdded;
        setterDouble.set(frame, value);
        return value;
      };
    }
    iData.putCalculation(result);
  }

  protected void traverseIncDecSuffix(
      ASTExpression expr,
      ASTExpression innerExpr,
      int valueToBeAdded
  ) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    innerExpr.accept(traverser);
    MICalculation innerCalc = iData.popCalculation();
    MISetter setter = iData.popSetter();
    MICalculation result;
    if (isStoredAsInt(exprType)) {
      MICalculationInt innerCalcInt = innerCalc.asCalculationInt();
      MISetterInt setterInt = setter.asSetterInt();
      result = (MICalculationInt) frame -> {
        int value = innerCalcInt.calculate(frame);
        setterInt.set(frame, value + valueToBeAdded);
        return value;
      };
    }
    else {
      MICalculationDouble innerCalcDouble = innerCalc.asCalculationDouble();
      MISetterDouble setterDouble = setter.asSetterDouble();
      result = (MICalculationDouble) frame -> {
        double value = innerCalcDouble.calculate(frame);
        setterDouble.set(frame, value + valueToBeAdded);
        return value;
      };
    }
    iData.putCalculation(result);
  }

}









