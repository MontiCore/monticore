/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton6;

import automaton5._ast.*;
import automaton6._ast.*;
import automaton6._visitor.*;
import automaton5.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomaton6)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *
 * @author Robert Heim
 */
public class Automaton6PrettyPrinter
		extends Automaton5PrettyPrinter 
		implements Automaton6Visitor {

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

