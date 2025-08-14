package de.monticore.interpreter.iterators;

import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

@FunctionalInterface
public interface MIForIterator {

  MIValue execute(ModelInterpreter interpreter, ASTMCStatement body);

}
