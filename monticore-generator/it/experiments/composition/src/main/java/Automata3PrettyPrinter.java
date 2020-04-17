/* (c) https://github.com/MontiCore/monticore */
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
public class Automata3PrettyPrinter
                        extends Automata3DelegatorVisitor {

 // ----------------------------------------------------------
  protected IndentPrinter out;

  public Automata3PrettyPrinter(IndentPrinter o) {
    out = o;

    // ... configured with three sublanguage visitors
    setInvAutomataVisitor(new InvAutomataPrettyPrinter(o));
    setExpressionVisitor(new ExpressionPrettyPrinter(o));
    setAutomata3Visitor(new Automata3SublangPP(o));
  }

  // ----------------------------------------------------------
  // Here we can override any visit, endVisit, ... method 
  // Which gets precedence over any (by default) delegated method:

  @Override
  public void visit(ASTAutomaton node) {
    out.println("/* print by composed Automata3PrettyPrinter */");
    out.println("automata " + node.getName() + " {");
    out.indent();
  }
}

