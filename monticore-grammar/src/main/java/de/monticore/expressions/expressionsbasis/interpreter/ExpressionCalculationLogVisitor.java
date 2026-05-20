// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.expressionsbasis.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.values.MCValue;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Stack;

import static de.monticore.interpreter.util.NativeStorageSelector.switchByFormat;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class ExpressionCalculationLogVisitor
    implements ExpressionsBasisVisitor2 {

  protected InterpreterData iData;

  public ExpressionCalculationLogVisitor(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  protected Stack<MIFrameLayoutForBasicSymbols> getScopeLayoutStack() {
    return iData.getFrameLayoutStack();
  }

  @Override
  public void endVisit(ASTExpression node) {
    if (iData.isPresentCalculation()) {
      SymTypeExpression exprType = normalize(typeOf(node));
      final MICalculation currentCalc = iData.popCalculation();
      final String info = node.get_SourcePositionStart()
          + " Type: " + exprType.printFullName();
      MICalculation calcWithInfo = switchByFormat(exprType,
          (MICalculationBoolean) frame -> printInfo(
              info,
              currentCalc.asCalculationBoolean().calculate(frame)
          ),
          (MICalculationInt) frame -> printInfo(
              info,
              currentCalc.asCalculationInt().calculate(frame)
          ),
          (MICalculationDouble) frame -> printInfo(
              info,
              currentCalc.asCalculationDouble().calculate(frame)
          ),
          (MICalculationValue) frame -> printInfo(
              info,
              currentCalc.asCalculationValue().calculate(frame)
          ),
          currentCalc.asCalculationVoid()
              .getChainedBefore(f -> printInfo(info))
      );
      iData.putCalculation(calcWithInfo);
    }
  }

  static <T> T printInfo(String info, T value) {
    Log.info(info + ": " + value, "Interpreter");
    return value;
  }

  static MCValue printInfo(String info, MCValue value) {
    Log.info(info + ": " + value.printValue(), "Interpreter");
    return value;
  }

  static void printInfo(String info) {
    Log.info(info + ": void", "Interpreter");
  }

}
