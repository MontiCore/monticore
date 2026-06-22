// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class ExpressionsAndStatementsInterpreter
    extends ExpressionsAndStatementsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  public ExpressionsAndStatementsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTBehaviorInput node) {
    MICalculation fullCalc;
    MICalculationVoid stmtChainCalc = MICalculationVoid.NOOP_CALC;
    for (ASTMCBlockStatement stmt : node.getMCBlockStatementList()) {
      stmt.accept(getTraverser());
      MICalculationVoid stmtCalc =
          iData.popCalculation().asCalculationVoid();
      stmtChainCalc = stmtChainCalc.getChainedBefore(stmtCalc);
    }
    if (node.isPresentExpression()) {
      TypeCheck3.typeOf(node.getExpression());
      node.getExpression().accept(getTraverser());
      MICalculation exprCalc = iData.popCalculation();
      fullCalc = stmtChainCalc.getChainedBefore(exprCalc);
    }
    else {
      fullCalc = stmtChainCalc;
    }
    iData.putCalculation(fullCalc);
  }

}
