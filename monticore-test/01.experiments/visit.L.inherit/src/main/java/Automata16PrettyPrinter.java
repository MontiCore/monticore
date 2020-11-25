/* (c) https://github.com/MontiCore/monticore */

import automata15._ast.*;
import automata16._ast.*;
import automata16._visitor.*;
import automata15.*;
import java.util.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomata16)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automata16PrettyPrinter  implements Automata16Visitor2 {

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

  @Override
  public void visit(automata16._ast.ASTTransition node) {
    print(node.getFrom() +" - " + node.getInput());
    if(node.isPresentOutput()) {
      print(" / " + node.getOutput());
    } 
    print(" > " + node.getTo());
    println(";");
  }
}

