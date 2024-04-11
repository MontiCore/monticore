/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;

import java.util.HashMap;

public interface ModelInterpreter {

  Value interpret(ASTNode n);

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  HashMap<ISymbol, Value> contextMap = new HashMap<>();

  default Value load(ISymbol s){
    return getRealThis().load(s);
  }

  default void store (ISymbol n, Value res){
    getRealThis().store(n,res);
  }

}
