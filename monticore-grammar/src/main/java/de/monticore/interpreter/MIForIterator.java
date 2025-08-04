package de.monticore.interpreter;

import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

public interface MIForIterator {
  
  public MIValue execute(ModelInterpreter interpreter, ASTMCStatement body);
  
}
