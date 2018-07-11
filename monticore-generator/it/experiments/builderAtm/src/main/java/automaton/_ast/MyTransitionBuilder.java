/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton._ast;

public class MyTransitionBuilder extends ASTTransitionBuilder {
  
  public ASTTransitionBuilder setInput(String input) {
    this.input = input + "Suf2";
    return this;
  }
  
}
