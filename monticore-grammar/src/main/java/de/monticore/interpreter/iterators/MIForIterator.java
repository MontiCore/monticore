package de.monticore.interpreter.iterators;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

@FunctionalInterface
public interface MIForIterator {

  MIValue execute(IModelInterpreter interpreter, ASTMCStatement body);

}
