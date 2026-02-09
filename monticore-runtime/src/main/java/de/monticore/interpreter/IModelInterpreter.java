package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Stack;

public interface IModelInterpreter {

  default MIValue interpret(ASTNode n) {
    String errorMsg = "0x57073 No implementation of ASTNode of type " + n.toString();
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  void setRealThis(IModelInterpreter realThis);

  IModelInterpreter getRealThis();

  Stack<MIScope> getScopeCallstack();

  default MIScope getCurrentScope() {
    return getRealThis().getScopeCallstack().peek();
  }

  default void pushScope(MIScope scope) {
    getRealThis().getScopeCallstack().push(scope);
  }

  default MIScope popScope() {
    return getRealThis().getScopeCallstack().pop();
  }

  default void declareFunction(ISymbol symbol, FunctionMIValue value) {
    getCurrentScope().declareFunction(symbol, value);
  }

  default MIValue loadFunction(ISymbol symbol) {
    return getCurrentScope().loadFunction(symbol);
  }

  default void declareVariable(ISymbol symbol, Optional<MIValue> value) {
    getCurrentScope().declareVariable(symbol, value);
  }

  default MIValue loadVariable(ISymbol symbol) {
    return getCurrentScope().loadVariable(symbol);
  }

  default void storeVariable(ISymbol symbol, MIValue value) {
    getCurrentScope().storeVariable(symbol, value);
  }


}
