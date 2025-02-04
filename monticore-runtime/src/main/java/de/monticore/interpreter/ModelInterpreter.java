/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symboltable.ISymbol;

import java.util.HashMap;
import java.util.Map;

public interface ModelInterpreter {

  default Value interpret(ASTNode n) {
    return new NotAValue();
  }

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  Map<ISymbol, Value> getContextMap();

  default Value load(ISymbol s){
    return getRealThis().load(s);
  }

  default void store (ISymbol n, Value res){
    getRealThis().store(n,res);
  }

}
