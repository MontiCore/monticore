/* (c) https://github.com/MontiCore/monticore */
package automata.visitors;

import automata._ast.ASTState;
import automata._visitor.AutomataVisitor2;

/**
 * Counts the states of an automaton.
 *
 */
public class CountStates implements AutomataVisitor2 {
  private int count = 0;
  
  @Override
  public void visit(ASTState node) {
    count++;
  }
  
  public int getCount() {
    return count;
  }
}
