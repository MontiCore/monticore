/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.values.ErrorValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

public interface ModelInterpreter {
  
  default Value interpret(ASTNode n) {
    String errorMsg = "No implementation of ASTNode of type " + n.toString();
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  MIScope getCurrentScope();
  
  void pushScope(MIScope scope);
  void popScope();

  default void declareVariable(ISymbol symbol, Value value) {
    getCurrentScope().declareVariable(symbol, value);
  }
  
  default Value load(ISymbol symbol) {
    return getRealThis().load(symbol);
  }

  default void store (ISymbol symbol, Value value){
    getRealThis().store(symbol, value);
  }

}
