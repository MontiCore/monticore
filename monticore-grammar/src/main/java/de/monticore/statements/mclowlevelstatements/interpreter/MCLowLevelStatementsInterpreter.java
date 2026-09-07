// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mclowlevelstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.signals.MCSignalBreak;
import de.monticore.interpreter.signals.MCSignalContinue;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsInheritanceHandler;

/**
 * Interpreter Visitor for MCLowLevelStatements
 */
public class MCLowLevelStatementsInterpreter
    extends MCLowLevelStatementsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  public MCLowLevelStatementsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTLabelledBreakStatement node) {
    MICalculationVoid breakCalc;
    if (node.isPresentLabel()) {
      final String label = node.getLabel();
      breakCalc = frame -> MCSignalBreak.signal(label);
    }
    else {
      breakCalc = frame -> MCSignalBreak.signal();
    }
    iData.putCalculation(breakCalc);
  }

  @Override
  public void traverse(ASTContinueStatement node) {
    MICalculationVoid continueCalc;
    if (node.isPresentLabel()) {
      final String label = node.getLabel();
      continueCalc = frame -> MCSignalContinue.signal(label);
    }
    else {
      continueCalc = frame -> MCSignalContinue.signal();
    }
    iData.putCalculation(continueCalc);
  }

}
