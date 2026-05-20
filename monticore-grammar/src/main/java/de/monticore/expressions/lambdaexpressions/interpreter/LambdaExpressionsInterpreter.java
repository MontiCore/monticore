// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.lambdaexpressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsInheritanceHandler;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.values.MIValueFunctionOfModel;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;

/**
 * Interpreter Visitor for LambdaExpressions
 */
public class LambdaExpressionsInterpreter
    extends LambdaExpressionsInheritanceHandler {

  protected InterpreterData iData;

  public LambdaExpressionsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  // LambdaExpressionBody does not need its own traversal method

  @Override
  public void traverse(ASTLambdaExpression lambda) {
    // parameters
    MIFrameLayoutForBasicSymbols lambdaScopeLayout =
        new MIFrameLayoutForBasicSymbols(
            iData.getFrameLayoutStack().peek()
        );
    List<VariableSymbol> paramSyms = lambda.getLambdaParameters()
        .streamLambdaParameters()
        .map(ASTLambdaParameter::getSymbol)
        .toList();
    for (VariableSymbol par : paramSyms) {
      lambdaScopeLayout.declareVariable(par);
    }
    List<MISetter> paramSetters = paramSyms.stream()
        .map(lambdaScopeLayout::getVariableSetter)
        .toList();

    // body
    iData.getFrameLayoutStack().push(lambdaScopeLayout);
    lambda.getLambdaBody().accept(getTraverser());
    MICalculation bodyCalc = iData.popCalculation();
    iData.getFrameLayoutStack().pop();

    // lambda
    MICalculationValue calc = currentFrame ->
        new MIValueFunctionOfModel(
            currentFrame, lambdaScopeLayout, paramSetters, bodyCalc
        );
    iData.putCalculation(calc);
  }

}
