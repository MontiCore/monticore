package automaton16;

import automaton15._ast.*;
import automaton16._ast.*;
import automaton16._visitor.*;
import automaton15.*;
import java.util.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomaton16)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automaton16PrettyPrinter
		extends Automaton15PrettyPrinter 
		implements Automaton16Visitor {

  @Override
  public void visit(ASTAutomaton node) {
    println("/* Printed in Version 16 */");
    println("automaton " + node.getName() + " {");
    indent();
  }
  
  @Override
  public void visit(automaton16._ast.ASTTransition node) {
    print(node.getFrom() +" - " + node.getInput());
    if(node.isPresentOutput()) {
      print(" / " + node.getOutput());
    } 
    print(" > " + node.getTo());
    println(";");
  }
}

