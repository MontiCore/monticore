/* (c) https://github.com/MontiCore/monticore */
import automata3._ast.ASTInvariant;
import automata3._visitor.Automata3DelegatorVisitor;

/**
 * 
 */
public class Automata3ComposedVisit extends Automata3DelegatorVisitor {

  // ----------------------------------------------------------
  // Typical visit/endvisit methods:

  @Override
  public void visit(ASTInvariant node) {
    System.out.println(" C3-ASTInvariant "); 
  }
  
  @Override
  public void endVisit(ASTInvariant node) {
    System.out.println("   C3-/Invariant ");
  }

}

