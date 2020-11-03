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
                extends Automata5PrettyPrinter
                implements Automata6Visitor {

  @Override
  public void visit(ASTAutomaton node) {
    println("/* Printed in Version 6 */");
    println("automaton " + node.getName() + " {");
    indent();
  }
  
  @Override
  public void visit(ASTTransitionWithOutput node) {
    print(node.getFrom());
    print(" - " + node.getInput() +" / " + node.getOutput() +" > ");
    print(node.getTo());
    println(";");
  }
}

