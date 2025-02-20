/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

public interface ModelInterpreter {
  
  default MIValue interpret(ASTNode n) {
    String errorMsg = "No implementation of ASTNode of type " + n.toString();
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  MIScope getCurrentScope();
  
  void pushScope(MIScope scope);
  void popScope();

  default void declareVariable(ISymbol symbol, MIValue value) {
    getCurrentScope().declareVariable(symbol, value);
  }
  
  default MIValue load(ISymbol symbol) {
    return getRealThis().load(symbol);
  }

  default void store (ISymbol symbol, MIValue value){
    getRealThis().store(symbol, value);
  }

}
