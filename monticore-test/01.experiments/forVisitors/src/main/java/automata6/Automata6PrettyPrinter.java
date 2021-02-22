/* (c) https://github.com/MontiCore/monticore */
package automata6;

import automata5._ast.*;
import automata6._ast.*;
import automata6._visitor.*;
import automata5.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomata6)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automata6PrettyPrinter
                implements Automata6Visitor2 {

  protected String result = "";

  protected int indention = 0;

  protected String indent = "";

  /**
   * Gets the printed result.
   *
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.result;
  }

  @Override
  public void visit(ASTTransitionWithOutput node) {
    print(node.getFrom());
    print(" - " + node.getInput() +" / " + node.getOutput() +" > ");
    print(node.getTo());
    println(";");
  }

  // the following part manages indentation -----------------

  protected void print(String s) {
    result += (indent + s);
    indent = "";
  }

  protected void println(String s) {
    result += (indent + s + "\n");
    indent = "";
    calcIndention();
  }

  protected void calcIndention() {
    indent = "";
    for (int i = 0; i < indention; i++) {
      indent += "  ";
    }
  }

  protected void indent() {
    indention++;
    calcIndention();
  }

  protected void unindent() {
    indention--;
    calcIndention();
  }
}

