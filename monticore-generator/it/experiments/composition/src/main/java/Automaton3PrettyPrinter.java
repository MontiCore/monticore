import automaton3._ast.*;
import automaton3._visitor.*;
import de.monticore.prettyprint.IndentPrinter;
import invautomaton._ast.*;
import invautomaton._visitor.*;
import expression._ast.*;
import expression._visitor.*;

/**
 * 
 */
public class Automaton3PrettyPrinter
			extends Automaton3DelegatorVisitor {

 // ----------------------------------------------------------
  protected IndentPrinter out;

  public Automaton3PrettyPrinter(IndentPrinter o) {
    out = o;

    // ... configured with three sublanguage visitors
    setInvAutomatonVisitor(new InvAutomatonPrettyPrinter(o));
    setExpressionVisitor(new ExpressionPrettyPrinter(o));
    setAutomaton3Visitor(new Automaton3SublangPP(o));
  }

  // ----------------------------------------------------------
  // Here we can override any visit, endVisit, ... method 
  // Which gets precedence over any (by default) delegated method:

  @Override
  public void visit(ASTAutomaton node) {
    out.println("/* print by composed Automaton3PrettyPrinter */");
    out.println("automaton " + node.getName() + " {");
    out.indent();
  }
}

