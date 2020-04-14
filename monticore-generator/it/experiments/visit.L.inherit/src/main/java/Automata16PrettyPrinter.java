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
public class Automata16PrettyPrinter
                extends Automata15PrettyPrinter
                implements Automata16Visitor {

  @Override
  public void visit(ASTAutomaton node) {
    println("/* Printed in Version 16 */");
    println("automaton " + node.getName() + " {");
    indent();
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

