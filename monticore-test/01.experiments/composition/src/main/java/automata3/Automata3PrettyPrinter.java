/* (c) https://github.com/MontiCore/monticore */
package automata3;
import automata3._visitor.*;
import de.monticore.prettyprint.IndentPrinter;
import invautomata._ast.*;

/**
 * 
 */
public class Automata3PrettyPrinter {

 // ----------------------------------------------------------
  protected IndentPrinter out;
  protected Automata3Traverser traverser;

  public Automata3PrettyPrinter(IndentPrinter o) {
    out = o;
    traverser = Automata3Mill.traverser();
    
    // ... configured with three sublanguage visitors
    traverser.add4InvAutomata(new InvAutomataSublangPP(o));
    ExpressionSublangPP espp = new ExpressionSublangPP(o);
    traverser.add4Expression(espp);
    traverser.add4Automata3(new Automata3SublangPP(o));
    
    // add expression sublanguage visitor also as handler 
    // as it provides a custom handle strategy
    traverser.setExpressionHandler(espp);
  }
  
  public String print(ASTAutomaton ast) {
    ast.accept(traverser);
    return out.getContent();
  }

}

