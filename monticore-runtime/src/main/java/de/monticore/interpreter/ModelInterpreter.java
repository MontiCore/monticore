/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;

public interface ModelInterpreter {

  Value interpret(ASTNode n);

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

  default Value load(ISymbol s){
    return getRealThis().load(s);
  }
  default void store (ISymbol n, Value res){
    getRealThis().store(n,res);
  }

}
