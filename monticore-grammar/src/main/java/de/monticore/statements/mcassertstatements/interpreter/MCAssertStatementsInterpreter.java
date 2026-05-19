// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcassertstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsInheritanceHandler;

/**
 * Interpreter Visitor for MCAssertStatements
 */
public class MCAssertStatementsInterpreter
    extends MCAssertStatementsInheritanceHandler {

  protected InterpreterData iData;

  public MCAssertStatementsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTAssertStatement node) {
    node.getAssertion().accept(getTraverser());
    MICalculationBoolean assertionCalc =
        iData.popCalculation().asCalculationBoolean();

    MICalculationVoid assertCalc;
    if (node.isPresentMessage()) {
      node.getMessage().accept(getTraverser());
      MICalculationValue messageCalc =
          iData.popCalculation().asCalculationValue();
      assertCalc = frame -> {
        if (!assertionCalc.calculate(frame)) {
          throw new AssertionError(
              messageCalc.calculate(frame).asNativeObject()
          );
        }
      };
    }
    else {
      assertCalc = frame -> {
        if (!assertionCalc.calculate(frame)) {
          throw new AssertionError();
        }
      };
    }
    iData.putCalculation(assertCalc);
  }

}
