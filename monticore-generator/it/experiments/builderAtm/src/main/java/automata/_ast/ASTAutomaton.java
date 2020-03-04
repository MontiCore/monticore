/* (c) https://github.com/MontiCore/monticore */
package automata._ast;

import java.util.List;

public class ASTAutomaton extends ASTAutomatonTOP implements  ASTAutomataNode
{
  
  protected ASTAutomaton() {
  }

  protected ASTAutomaton(String name, List<ASTState> states,
    		 List<ASTTransition> transitions)  
  {
    setName(name);
    setStateList(states);
    setTransitionList(transitions);
  }
  
}
