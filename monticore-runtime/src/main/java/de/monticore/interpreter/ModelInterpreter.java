/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.ast.ASTNode;

public interface ModelInterpreter {

  Value interpret(ASTNode n);

  void setRealThis(ModelInterpreter realThis);

  ModelInterpreter getRealThis();

}
