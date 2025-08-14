package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.values.ErrorMIValue;
import de.se_rwth.commons.logging.Log;

public interface IModelInterpreter {

  default MIValue interpret(ASTNode n) {
    String errorMsg = "0x57073 No implementation of ASTNode of type " + n.toString();
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  void popScope();

  void pushScope(IMIScope scope);

}
