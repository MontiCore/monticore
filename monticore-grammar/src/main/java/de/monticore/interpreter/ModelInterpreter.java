/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.symboltable.ISymbol;

public interface ModelInterpreter extends IModelInterpreter {

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
