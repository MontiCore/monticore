/* (c) https://github.com/MontiCore/monticore */
package invautomata;
import invautomata._ast.*;
import invautomata._visitor.*;

/**
 * Visitor für die Sprache InvAutomata
 *
 * In aktueller Fassung kompilierbar, aber nicht nutzbar,
 * da InvAutomata eine component grammar ist und nicht direkt
 * AST nodes aufbauen kann.
 */
public class InvAutomataCheapVisit implements InvAutomataVisitor2 {

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTAutomaton node) {
    System.out.println("/* printed with " + this.getClass()+ " */");
    System.out.println("IA- automaton " + node.getName() + " {");
  }
  
  @Override
  public void visit(ASTState node) {
    System.out.print("IA-  state " + node.getName() +" ");
    if (node.isInitial()) {
      System.out.print("<<initial>>");
    }
    if (node.isFinal()) {
      System.out.print("<<final>>");
    }
    System.out.println(";");
  }

  @Override
  public void visit(ASTTransition node) {
    System.out.print("IA-  "+ node.getFrom());
    System.out.print(" - " + node.getInput() + " > ");
    System.out.print(node.getTo());
    System.out.println(";");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTAutomaton node) {
    System.out.println("    IA-/Automaton }");
  }

  @Override
  public void endVisit(ASTState node) {
    System.out.println("    IA-/State ");
  }

  @Override
  public void endVisit(ASTTransition node) {
    System.out.println("    IA-/Transition ");
  }
  
}

