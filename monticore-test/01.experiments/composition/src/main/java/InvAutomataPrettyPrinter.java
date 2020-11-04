/* (c) https://github.com/MontiCore/monticore */
import de.monticore.prettyprint.IndentPrinter;
import invautomata._ast.*;
import invautomata._visitor.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTInvAutomata)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class InvAutomataPrettyPrinter
                        implements InvAutomataVisitor {

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  InvAutomataVisitor realThis = this;

  @Override
  public void setRealThis(InvAutomataVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public InvAutomataVisitor getRealThis() {
    return realThis;
  }

  // ----------------------------------------------------------
  protected IndentPrinter out;

  public InvAutomataPrettyPrinter(IndentPrinter o) {
    out = o;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTAutomaton node) {
    out.println("/* printed with InvAutomataPrettyPrinter */");
    out.println("automaton " + node.getName() + " {");
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
