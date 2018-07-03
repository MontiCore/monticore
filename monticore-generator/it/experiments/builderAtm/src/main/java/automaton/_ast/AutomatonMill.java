/* (c) Monticore license: https://github.com/MontiCore/monticore */

package automaton._ast;

public class AutomatonMill extends AutomatonMillTOP {
  
  @Override
  protected ASTTransitionBuilder _transitionBuilder() {
     return new MyTransitionBuilder();
  }

}
