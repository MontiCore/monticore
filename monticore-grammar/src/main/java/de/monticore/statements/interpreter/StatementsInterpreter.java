// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.interpreter;

import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.symbols.basicsymbols.interpreter.AbstractInterpreterForBasicSymbols;
import de.monticore.values.MCValue;
import de.monticore.visitor.ITraverser;

/**
 * Interpreter for languages that have statements
 */
public class StatementsInterpreter extends AbstractInterpreterForBasicSymbols {

  public StatementsInterpreter(
      ITraverser traverser,
      InterpreterDataForBasicSymbols iData
  ) {
    super(traverser, iData);
  }

  public MCValue interpret(ASTMCStatement statement) {
    return interpretNode(statement);
  }

}
