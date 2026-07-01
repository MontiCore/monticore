// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.interpreter.ExpressionsInterpreter;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.values.MCValue;
import de.monticore.visitor.ITraverser;

/**
 * To access the internals
 */
public class InterpreterAccess4Tests extends ExpressionsInterpreter {

  public InterpreterAccess4Tests(ITraverser traverser, InterpreterDataForBasicSymbols iData) {
    super(traverser, iData);
  }

  public ITraverser getTraverser() {
    return traverser;
  }

  public InterpreterDataForBasicSymbols getInterpreterData() {
    return iData;
  }

  @Override
  public MICalculation getCalculation(ASTNode node) {
    return super.getCalculation(node);
  }

  @Override
  public MCValue interpretNode(ASTNode node) {
    return super.interpretNode(node);
  }

}
