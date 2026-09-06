// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.interpreter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.basicsymbols.interpreter.AbstractInterpreterForBasicSymbols;
import de.monticore.values.MCValue;
import de.monticore.visitor.ITraverser;

/**
 * API to use the interpreter.
 * This Version is only for languages
 * that solely have expressions to interpret.
 */
public class ExpressionsInterpreter extends AbstractInterpreterForBasicSymbols {

  public ExpressionsInterpreter(
      ITraverser traverser,
      InterpreterDataForBasicSymbols iData
  ) {
    super(traverser, iData);
  }

  // interpretation

  public MCValue interpret(ASTExpression expression) {
    return interpretNode(expression);
  }

  public MCValue interpret(ASTLiteral literal) {
    return interpretNode(literal);
  }

}
