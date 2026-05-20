// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.values.MCValue;
import de.monticore.statements.interpreter.StatementsInterpreter;
import de.monticore.visitor.ITraverser;

/**
 * To access the internals
 */
public class InterpreterAccess4Tests extends StatementsInterpreter {

  public InterpreterAccess4Tests(TraverserAndIData interpreterTraverser) {
    super(interpreterTraverser);
  }

  public ITraverser getTraverser() {
    return interpreterTraverser.traverser();
  }

  public InterpreterData getInterpreterData() {
    return interpreterTraverser.data();
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
