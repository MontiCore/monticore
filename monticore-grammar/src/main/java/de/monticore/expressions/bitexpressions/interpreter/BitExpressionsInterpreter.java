// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.bitexpressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsInheritanceHandler;
import de.monticore.expressions.interpreter.util.InterpreterOperatorTraverser;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.util.InterpreterVisitorOperatorCalculator;

/**
 * Interpreter Visitor for BitExpressions
 */
public class BitExpressionsInterpreter
    extends BitExpressionsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  protected InterpreterVisitorOperatorCalculator opCalculator =
      new InterpreterVisitorOperatorCalculator();
  protected InterpreterOperatorTraverser opTraverser =
      new InterpreterOperatorTraverser();

  public BitExpressionsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTLeftShiftExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleLeftShift
    );
  }

  @Override
  public void traverse(ASTRightShiftExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleRightShift
    );
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleLogicalRightShift
    );
  }

  @Override
  public void traverse(ASTBinaryAndExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleBinaryAnd
    );
  }

  @Override
  public void traverse(ASTBinaryXorExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleBinaryXor
    );
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleBinaryOr
    );
  }

}
