// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.interpretertestlang.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.tests.interpretertestlang._ast.ASTInterpreterInput;
import de.monticore.tests.interpretertestlang._ast.ASTStatement;
import de.monticore.tests.interpretertestlang._visitor.InterpreterTestLangInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class InterpreterTestLangInterpreter
    extends InterpreterTestLangInheritanceHandler {

  protected InterpreterData iData;

  public InterpreterTestLangInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTInterpreterInput node) {
    MICalculation fullCalc;
    MICalculationVoid stmtChainCalc = MICalculationVoid.NOOP_CALC;
    for (ASTStatement stmt : node.getStatementList()) {
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
