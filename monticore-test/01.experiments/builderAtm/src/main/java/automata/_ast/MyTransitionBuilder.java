/* (c) https://github.com/MontiCore/monticore */
package automata._ast;

public class MyTransitionBuilder extends ASTTransitionBuilder {
  
  public ASTTransitionBuilder setInput(String input) {
    this.input = input + "Suf2";
    return this;
  }
  
}
