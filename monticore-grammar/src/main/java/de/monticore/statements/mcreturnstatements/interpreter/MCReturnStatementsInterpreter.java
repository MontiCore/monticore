// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcreturnstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.values.MCSignalReturnForInterpreter;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsInheritanceHandler;

/**
 * Interpreter Visitor for MCReturnStatements
 */
public class MCReturnStatementsInterpreter
    extends MCReturnStatementsInheritanceHandler {

  protected InterpreterData iData;

  public MCReturnStatementsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTReturnStatement node) {
    MICalculationVoid returnSignalCalc;
    if (node.isPresentExpression()) {
      node.getExpression().accept(getTraverser());
      MICalculationValue exprCalc =
          iData.popCalculation().asCalculationValue();
      returnSignalCalc = frame ->
          MCSignalReturnForInterpreter.signal(exprCalc.calculate(frame));
    }
    else {
      returnSignalCalc = frame ->
          MCSignalReturnForInterpreter.signal();
    }
    iData.putCalculation(returnSignalCalc);
  }

}
