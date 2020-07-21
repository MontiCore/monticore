/* (c) https://github.com/MontiCore/monticore */

package automata;

import automata._ast.ASTTransitionBuilder;
import automata._ast.MyTransitionBuilder;

public class AutomataMill extends AutomataMillTOP {
  
  @Override
  protected ASTTransitionBuilder _transitionBuilder() {
     return new MyTransitionBuilder();
  }

}
