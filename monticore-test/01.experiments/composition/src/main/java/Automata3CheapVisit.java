/* (c) https://github.com/MontiCore/monticore */
import automata3._ast.ASTInvariant;
import automata3._visitor.Automata3Visitor;
import expression._ast.ASTAnd;
import expression._ast.ASTNot;
import expression._ast.ASTTruth;
import expression._ast.ASTVariable;
import invautomata._ast.ASTAutomaton;
import invautomata._ast.ASTState;
import invautomata._ast.ASTTransition;

/**
 * Visitor for the composed language  Automata3
 *
 * but the vistor has been defined in a monolithic form
 * by copy pasting the content of two visitors from the sub languages.
 * (actually this is a bad an cheap form of reuse)
 */
public class Automata3CheapVisit implements Automata3Visitor {

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  Automata3Visitor realThis = this;

  @Override
  public void setRealThis(Automata3Visitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public Automata3Visitor getRealThis() {
    return realThis;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTInvariant node) {
    System.out.println(" X3-ASTInvariant "); 
  }
  
  @Override
  public void endVisit(ASTInvariant node) {
    System.out.println("   X3-/Invariant ");
  }

  // ----------------------------------------------------------
  // ----------------------------------------------------------
  @Override
  public void visit(ASTTruth node) {
    System.out.println(" X3-ASTTruth " + node.isTt()+ "," + node.isFf()); 
  }
  
  @Override
  public void visit(ASTNot node) {
    System.out.println(" X3-ASTNot ");
  }
  
  @Override
  public void visit(ASTVariable node) {
    System.out.println(" X3-ASTVariable " + node.getName());
  }
  
  @Override
  public void visit(ASTAnd node) {
    System.out.println(" X3-ASTAnd ");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTTruth node) {
    System.out.println("   X3-/Truth");
  }

  @Override
  public void endVisit(ASTNot node) {
    System.out.println("   X3-/Not ");
  }

  @Override
  public void endVisit(ASTVariable node) {
    System.out.println("   X3-/Variable ");
  }

  @Override
  public void endVisit(ASTAnd node) {
    System.out.println("   X3-/And ");
  }
  

  // ----------------------------------------------------------
  @Override
  public void visit(ASTAutomaton node) {
    System.out.println("X3-/* printed with " + this.getClass()+ " */");
    System.out.println("X3-automaton " + node.getName() + " {");
  }
  
  @Override
  public void visit(ASTState node) {
    System.out.print(" X3-state " + node.getName() +" ");
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
    System.out.print(" X3-"+ node.getFrom());
    System.out.print(" - " + node.getInput() + " > ");
    System.out.print(node.getTo());
    System.out.println(";");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTAutomaton node) {
    System.out.println("    X3-/Automaton }");
  }

  @Override
  public void endVisit(ASTState node) {
    System.out.println("    X3-/State ");
  }

  @Override
  public void endVisit(ASTTransition node) {
    System.out.println("    X3-/Transition ");
  }
  
}

