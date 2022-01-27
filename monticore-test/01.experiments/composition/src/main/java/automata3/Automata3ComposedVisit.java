/* (c) https://github.com/MontiCore/monticore */
package automata3;
import automata3.Automata3Handle;
import automata3.Automata3Mill;
import automata3.Automata3Visit;
import automata3._visitor.Automata3Traverser;
import automata3._visitor.Automata3Visitor2;
import invautomata._ast.ASTAutomaton;

/**
 * Composing visitors in a traverser.
 */
public class Automata3ComposedVisit {
  
  public void compute(ASTAutomaton ast) {
    Automata3Traverser traverser = Automata3Mill.traverser();
    
    // add visitors and handlers (here: one of each for the Automata3 language)
    Automata3Visitor2 v3 = new Automata3Visit();
    Automata3Handle h3 = new Automata3Handle();
    
    traverser.add4Automata3(v3);
    traverser.setAutomata3Handler(h3);
    
    ast.accept(traverser);
  }
}
