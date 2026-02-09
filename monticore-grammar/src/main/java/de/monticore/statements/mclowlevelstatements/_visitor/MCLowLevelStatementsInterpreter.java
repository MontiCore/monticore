package de.monticore.statements.mclowlevelstatements._visitor;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.MIContinueSignal;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.se_rwth.commons.logging.Log;

public class MCLowLevelStatementsInterpreter extends MCLowLevelStatementsInterpreterTOP {
  
  public MCLowLevelStatementsInterpreter() {}
  
  public MCLowLevelStatementsInterpreter(IModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public MIValue interpret(ASTContinueStatement node) {
    if (node.isPresentLabel()) {
      String errorMsg = "0x57085 Labels are not supported for Continue Statements.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    return new MIContinueSignal();
  }
}
