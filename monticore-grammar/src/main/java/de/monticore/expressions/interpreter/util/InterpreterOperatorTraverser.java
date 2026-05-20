// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.interpreter.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.visitor.ITraverser;

import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Helps traversal of unary and binary operators
 * for interpreter visitors.
 */
public class InterpreterOperatorTraverser {

  public void traverseUnaryOperator(
      ITraverser traverser,
      InterpreterDataForBasicSymbols iData,
      ASTExpression expr,
      ASTExpression innerExpr,
      UnaryOperationHandler handler
  ) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression innerExprType = normalize(typeOf(innerExpr));
    innerExpr.accept(traverser);
    MICalculation innerCalc = iData.popCalculation();
    MICalculation calc = handler.getCalc(innerCalc, innerExprType, exprType);
    iData.putCalculation(calc);
  }

  public void traverseBinaryOperator(
      ITraverser traverser,
      InterpreterDataForBasicSymbols iData,
      ASTExpression expr,
      ASTExpression leftExpr,
      ASTExpression rightExpr,
      BinaryOperationHandler handler
  ) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression leftType = normalize(typeOf(leftExpr));
    leftExpr.accept(traverser);
    MICalculation leftCalc = iData.popCalculation();
    SymTypeExpression rightType = normalize(typeOf(rightExpr));
    rightExpr.accept(traverser);
    MICalculation rightCalc = iData.popCalculation();
    MICalculation opCalc = handler.getCalc(
        leftCalc, rightCalc, leftType, rightType, exprType
    );
    iData.putCalculation(opCalc);
  }

  /**
   * Represents a function that takes one calculation and its type,
   * and returns a calculation for the operator, e.g.,
   * {@code -(1)}.
   * Here, a {@link UnaryOperationHandler} for "-" can be created.
   * <p>
   * This exists solely to ease the writing of interpreter visitors.
   */
  @FunctionalInterface
  public interface UnaryOperationHandler {
    MICalculation getCalc(
        MICalculation innerCalc,
        SymTypeExpression innerType,
        SymTypeExpression exprType
    );
  }

  /**
   * Represents a function that takes two calculations and their types,
   * and returns a calculation for the operator, e.g.,
   * {@code 1 + 2.4}.
   * Here, a {@link BinaryOperationHandler} for "+" can be created.
   * <p>
   * This exists solely to ease the writing of interpreter visitors.
   */
  @FunctionalInterface
  public interface BinaryOperationHandler {
    MICalculation getCalc(
        MICalculation leftCalc,
        MICalculation rightCalc,
        SymTypeExpression leftType,
        SymTypeExpression rightType,
        SymTypeExpression exprType
    );
  }
}
