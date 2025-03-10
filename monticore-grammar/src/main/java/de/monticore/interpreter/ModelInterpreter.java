/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

public interface ModelInterpreter extends IModelInterpreter {

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  MIScope getCurrentScope();
  
  void pushScope(MIScope scope);
  void popScope();

  default void declareFunction(FunctionSymbol symbol, FunctionMIValue value) {
    getCurrentScope().declareFunction(symbol, value);
  }
  
  default MIValue loadFunction(FunctionSymbol symbol) {
    return getRealThis().loadFunction(symbol);
  }
  
  default void declareVariable(VariableSymbol symbol, MIValue value) {
    getCurrentScope().declareVariable(symbol, value);
  }
  
  default MIValue loadVariable(VariableSymbol symbol) {
    return getRealThis().loadVariable(symbol);
  }

  default void storeVariable (VariableSymbol symbol, MIValue value){
    getRealThis().storeVariable(symbol, value);
  }

}
