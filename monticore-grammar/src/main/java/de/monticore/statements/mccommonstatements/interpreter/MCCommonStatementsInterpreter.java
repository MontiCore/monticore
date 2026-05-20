// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mccommonstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.values.MCSignalBreakForInterpreter;
import de.monticore.interpreter.values.MCSignalContinueForInterpreter;
import de.monticore.statements.mccommonstatements._ast.ASTBreakStatement;
import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._ast.ASTEmptyStatement;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;

/**
 * Interpreter Visitor for MCCommonStatements
 */
public class MCCommonStatementsInterpreter
    extends MCCommonStatementsInheritanceHandler {

  protected InterpreterData iData;

  public MCCommonStatementsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTMCJavaBlock node) {
    // note that this does not open a scope by itself.

    // avoiding a loop by chaining the statements,
    // This is supposedly faster (Edit: it is in my tests)
    // (cf. "Efficient hosted interpreters on the JVM")
    MICalculationVoid calcChain = MICalculationVoid.NOOP_CALC;
    for (int i = 0; i < node.sizeMCBlockStatements(); i++) {
      node.getMCBlockStatement(i).accept(getTraverser());
      MICalculationVoid stmtCalc =
          iData.popCalculation().asCalculationVoid();
      calcChain = calcChain.getChainedBefore(stmtCalc);
    }
    iData.putCalculation(calcChain);
  }

  @Override
  public void traverse(ASTIfStatement node) {
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    node.getThenStatement().accept(getTraverser());
    MICalculationVoid thenCalc =
        iData.popCalculation().asCalculationVoid();
    MICalculationVoid elseCalc;
    if (node.isPresentElseStatement()) {
      node.getElseStatement().accept(getTraverser());
      elseCalc = iData.popCalculation().asCalculationVoid();
    }
    else {
      elseCalc = MICalculationVoid.NOOP_CALC;
    }
    MICalculationVoid ifCalc = frame -> {
      if (conditionCalc.calculate(frame)) {
        thenCalc.calculate(frame);
      }
      else {
        elseCalc.calculate(frame);
      }
    };
    iData.putCalculation(ifCalc);
  }

  @Override
  public void traverse(ASTWhileStatement node) {
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();
    MICalculationVoid whileCalc = frame -> {
      while (conditionCalc.calculate(frame)) {
        try {
          bodyCalc.calculate(frame);
        }
        catch (MCSignalBreakForInterpreter ignored) {
          break;
        }
        catch (MCSignalContinueForInterpreter ignored) {
          // no-op
        }
      }
    };
    iData.putCalculation(whileCalc);
  }

  @Override
  public void traverse(ASTDoWhileStatement node) {
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    MICalculationVoid doWhileCalc = frame -> {
      do {
        try {
          bodyCalc.calculate(frame);
        }
        catch (MCSignalBreakForInterpreter ignored) {
          break;
        }
        catch (MCSignalContinueForInterpreter ignored) {
          // no-op
        }
      }
      while (conditionCalc.calculate(frame));
    };
    iData.putCalculation(doWhileCalc);
  }

  @Override
  public void traverse(ASTExpressionStatement node) {
    // one could make it void, but there is no reason to
    node.getExpression().accept(getTraverser());
  }

  @Override
  public void traverse(ASTEmptyStatement node) {
    iData.putCalculation(MICalculationVoid.NOOP_CALC);
  }

  @Override
  public void traverse(ASTBreakStatement node) {
    MICalculationVoid breakCalc = frame ->
        MCSignalBreakForInterpreter.signal();
    iData.putCalculation(breakCalc);
  }

}
