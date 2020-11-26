/* (c) https://github.com/MontiCore/monticore */
import automata3._ast.ASTInvariant;
import automata3._visitor.Automata3Visitor2;
import expression._ast.ASTAnd;
import expression._ast.ASTNot;
import expression._ast.ASTTruth;
import expression._ast.ASTVariable;
import invautomata._ast.ASTAutomaton;
import invautomata._ast.ASTState;
import invautomata._ast.ASTTransition;

/**
 * Visitor for the composed language Automata3. 
 * 
 * But the visitor contains only the content of the elements defined on
 * Automata3 level. Nodes from super languages must be visited by their
 * respective visitors providing the corresponding visit and endVisit 
 * methods.
 */
public class Automata3Visit implements Automata3Visitor2 {

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

}

