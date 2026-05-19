// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.interpreter;

import de.monticore.expressions.interpreter.ExpressionsInterpreter;
import de.monticore.interpreter.util.TraverserAndIData;
import de.monticore.interpreter.values.MIValue;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

/**
 * Interpreter for languages that have statements
 */
public class StatementsInterpreter extends ExpressionsInterpreter {

  public StatementsInterpreter(TraverserAndIData interpreterTraverser) {
    super(interpreterTraverser);
  }

  public MIValue interpret(ASTMCStatement statement) {
    return interpretNode(statement);
  }

}
