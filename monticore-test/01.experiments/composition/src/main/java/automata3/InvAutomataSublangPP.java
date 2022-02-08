/* (c) https://github.com/MontiCore/monticore */
package automata3;
import de.monticore.prettyprint.IndentPrinter;
import invautomata._ast.*;
import invautomata._visitor.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTInvAutomata)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class InvAutomataSublangPP
                        implements InvAutomataVisitor2 {

  protected IndentPrinter out;

  public InvAutomataSublangPP(IndentPrinter o) {
    out = o;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:
  
  @Override
  public void visit(ASTAutomaton node) {
    out.println("/* print by composed Automata3PrettyPrinter */");
    out.println("automata " + node.getName() + " {");
    out.indent();
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    out.unindent();
    out.println("}");
  }
  
  @Override
  public void visit(ASTState node) {
    out.print("state " + node.getName() +" ");
  }

  @Override
  public void endVisit(ASTState node) {
    if (node.isInitial()) {
      out.print("<<initial>> ");
    }
    if (node.isFinal()) {
      out.print("<<final>> ");
    }
    out.println(";");
  }
  
  @Override
  public void visit(ASTTransition node) {
    out.print(node.getFrom());
    out.print(" - " + node.getInput() + " > ");
    out.print(node.getTo());
    out.println(";");
  }
  
}
