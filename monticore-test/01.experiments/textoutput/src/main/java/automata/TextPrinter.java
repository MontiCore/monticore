/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.*;
import automata._visitor.AutomataHandler;
import automata._visitor.AutomataTraverser;
import automata._visitor.AutomataVisitor2;
import automata.visitors.CountStates;
import java.util.*;

/**
 * Prints automatoni in a textual "readable" form. 
 * Use {@link #print(ASTAutomaton)} to start a
 * print and get the result by using {@link #getResult()}.
 *

 */
public class TextPrinter implements AutomataVisitor2 , AutomataHandler {
  private String result = "";

  protected AutomataTraverser traverser;

  @Override
  public void setTraverser(AutomataTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public AutomataTraverser getTraverser() {
    return traverser;
  }

  /**
   * Prints the automaton
   * 
   * @param automaton
   */
  public void print(ASTAutomaton automaton) {
    handle(automaton);
  }
  
  /**
   * Gets the printed result.
   * 
   * @return the result of the Text print.
   */
  public String getResult() {
    return this.result;
  }
  
  @Override
  public void visit(ASTAutomaton node) {
    println("------------------------------- Section " + (rand.nextInt(12)+1) +".2");
    println("This automaton is called '" + node.getName() + "' and describes the");
    println("state space of equaly named class '" + node.getName() +"'.");
    print  ("The states are named as ");
    // to identify the end correctly we collect the number of states first
    CountStates cs = new CountStates();
    AutomataTraverser traverser = AutomataMill.traverser();
    traverser.add4Automata(cs);
    node.accept(traverser);
    stateCount = cs.getCount();
    stateNumber = stateCount;
  }
  
  private int stateNumber = 0;
  private int stateCount = 0;
  
  // we run several phases (orchestrated at the ASTAutomaton node)
  private int phase = 0;
  
  Random rand = new Random();
  
  // Nested states
  Stack<ASTState> statehierarchy = new Stack<>();
  
  @Override
  public void endVisit(ASTAutomaton node) {
    println("------------------------------- end of section");
  }
  
  @Override
  public void traverse(ASTAutomaton node) {
    // guarantee ordering: states before transitions
  
    // handling state list
    phase = 1;
    node.getStateList().stream().forEach(s -> s.accept(getTraverser()));
  
    // handle nesting: traversing states a second time
    phase = 2;
    if(stateNumber != node.sizeStates()) {
      println("The state space is hierarchically nested.");
    }
    statehierarchy = new Stack<>();
    node.getStateList().stream().forEach(s -> s.accept(getTraverser()));
  
    /// could be separately listed: initial and final
    phase = 3;
  
    // handle the transitions (cross all hierarchy)
    phase = 5;
    node.getTransitionList().stream().forEach(t -> t.accept(getTraverser()));
  }
  
  @Override
  public void traverse(ASTState node) {
    if(phase <= 4) {
      node.getStateList().stream().forEach(s -> s.accept(getTraverser()));
    } else if(phase >= 5) {
      node.getTransitionList().stream().forEach(t -> t.accept(getTraverser()));
    }
  }

  @Override
  public void visit(ASTState node) {
    switch(phase) {
      // list of states
      case 1:
        if (node.isInitial() && node.isFinal()) {
          print("initial and final state ");
        } else if (node.isInitial()) {
          print("initial state ");
        } else if (node.isFinal()) {
          print("final state ");
        }
        print(node.getName());
        if (stateCount > 2) {
          print(", ");
        } else if (stateCount == 2) {
          print(" and ");
        } else {
          println(".");
        }
        --stateCount;
        break;
          // nesting:
      case 2:
        if(!statehierarchy.empty()) {
          println("State " + node.getName() + " is contained in " + statehierarchy.peek().getName() + ".");
        }
        break;
    }
    statehierarchy.push(node);
  }
  
  @Override
  public void endVisit(ASTState node) {
    statehierarchy.pop();
  }
  
  @Override
  public void visit(ASTTransition node) {
    // Introduce variants (to make reading nicer, but actually for structured
    // and easy acessible texts that is not necessarily good.)
    switch(rand.nextInt(4)) {
      case 0:
        println("State " + node.getFrom() + " changes to " + node.getTo() +
                " when receiving input " + node.getInput() + ".");
        break;
      case 1:
        println("With input " + node.getInput() + " the state of the described" +
                " object changes its state from " + node.getFrom() + " to " + node.getTo() + ".");
        break;
      case 2:
        println("Processing " + node.getInput() + " the object transitions from" +
                " state " + node.getFrom() + " to " + node.getTo() + ".");
        break;
      case 3:
        println("State " + node.getFrom() + " and input " + node.getInput() +
                " map to new state "+ node.getTo() + ".");
        break;
    }
  }
  
  private void print(String s) {
    result += s;
  }
  
  private void println(String s) {
    result += s + "\n";
  }
  
}
