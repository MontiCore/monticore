/* (c) https://github.com/MontiCore/monticore */
import automata3.Automata3Mill;
import automata3._ast.*;
import automata3._visitor.*;
import de.monticore.prettyprint.IndentPrinter;
import invautomata._ast.*;
import invautomata._visitor.*;
import expression._ast.*;
import expression._visitor.*;

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
    ExpressionSublangPP espp = new ExpressionSublangPP(o);

    // ... configured with three sublanguage visitors
    traverser.addInvAutomataVisitor(new InvAutomataSublangPP(o));
    traverser.addExpressionVisitor(espp);
    traverser.addAutomata3Visitor(new Automata3SublangPP(o));
    
    // add expression sublanguage visitor also as handler 
    // as it provides a custom handle strategy
    traverser.setExpressionHandler(espp);
  }
  
  public String print(ASTAutomaton ast) {
    ast.accept(traverser);
    return out.getContent();
  }

}

