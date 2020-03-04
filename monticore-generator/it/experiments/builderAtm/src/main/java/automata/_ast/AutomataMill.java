/* (c) https://github.com/MontiCore/monticore */

package automata._ast;

public class AutomataMill extends AutomataMillTOP {
  
  @Override
  protected ASTTransitionBuilder _transitionBuilder() {
     return new MyTransitionBuilder();
  }

}
