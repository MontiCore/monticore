// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcreturnstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.signals.MCSignalReturn;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsInheritanceHandler;

/**
 * Interpreter Visitor for MCReturnStatements
 */
public class MCReturnStatementsInterpreter
    extends MCReturnStatementsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  public MCReturnStatementsInterpreter(InterpreterDataForBasicSymbols iData) {
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
          MCSignalReturn.signal(exprCalc.calculate(frame));
    }
    else {
      returnSignalCalc = frame ->
          MCSignalReturn.signal();
    }
    iData.putCalculation(returnSignalCalc);
  }

}
