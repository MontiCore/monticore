package de.monticore.expressions.lambdaexpressions._visitor;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.interpreter.values.func.MILambdaValue;

public class LambdaExpressionsInterpreter extends LambdaExpressionsInterpreterTOP {
  
  public LambdaExpressionsInterpreter() {
    super();
  }
  
  public LambdaExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public MIValue interpret(ASTLambdaExpression node) {
    return new MILambdaValue(getRealThis().getCurrentScope(), node);
  }
  
}
