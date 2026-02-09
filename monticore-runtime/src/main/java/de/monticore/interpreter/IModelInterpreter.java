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

  /*
  TODO Explicit cast is needed because:
    Short version: Dependencies between symbols, scopes, functions & interpreter
    Long version:
    ModelFunctionMIValue has VariableSymbols as attributes
      -> ModelFunctionMIValue must be in mc-grammar
    MIScope uses Variable-/FunctionSymbol
      -> must be in mc-grammar
    ModelFunctionMIValue uses 'pushScope(MIScope)';
    IModelInterpreter must in mc-rte and has 'MIValue interpret()'
      -> MIValue must be in mc-rte;
    MIValue has 'FunctionMIValue asFunction()'
      -> FunctionMIValue must be in mc-rte
    FunctionMIValue needs 'execute(IModelInterpreter)'
      -> ModelFunctionMIValue must use IModelInterpreter
      -> ModelInterpreter needs 'pushScope(MIScope)'
    MIScope cant be accessed -> IMIScope
    IMIscope cant access Variable-/FunctionSymbol -> explicit cast
   */
  default void pushScope(MIScope scope) {
    getRealThis().getScopeCallstack().push((MIScope) scope);
  }

  default void popScope() {
    getRealThis().getScopeCallstack().pop();
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
