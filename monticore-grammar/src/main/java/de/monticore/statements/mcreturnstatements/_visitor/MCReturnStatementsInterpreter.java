package de.monticore.statements.mcreturnstatements._visitor;

import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.values.MIReturnSignal;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;

public class MCReturnStatementsInterpreter extends MCReturnStatementsInterpreterTOP {
  
  public MCReturnStatementsInterpreter() {}
  
  public MCReturnStatementsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public MIValue interpret(ASTReturnStatement node) {
    if (node.isPresentExpression()) {
      MIValue returnValue = node.getExpression().evaluate(getRealThis());
      if (returnValue.isFlowControlSignal()) return returnValue;
      
      return new MIReturnSignal(returnValue);
    } else {
      return new MIReturnSignal();
    }
  }
}
