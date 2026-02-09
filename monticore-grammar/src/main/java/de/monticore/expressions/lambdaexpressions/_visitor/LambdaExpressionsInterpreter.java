package de.monticore.expressions.lambdaexpressions._visitor;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpressionBody;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ModelFunctionMIValue;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionsInterpreter extends LambdaExpressionsInterpreterTOP {

  public LambdaExpressionsInterpreter() {
    super();
  }

  public LambdaExpressionsInterpreter(IModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public MIValue interpret(ASTLambdaExpressionBody node) {
    return node.getExpression().evaluate(getRealThis());
  }

  @Override
  public MIValue interpret(ASTLambdaExpression node) {
    List<VariableSymbol> parameterSymbols = new ArrayList<>();
    for (ASTLambdaParameter parameter : node.getLambdaParameters().getLambdaParameterList()) {
      parameterSymbols.add(parameter.getSymbol());
    }
    return new ModelFunctionMIValue(getRealThis().getCurrentScope(), parameterSymbols, node.getLambdaBody());
  }

}
