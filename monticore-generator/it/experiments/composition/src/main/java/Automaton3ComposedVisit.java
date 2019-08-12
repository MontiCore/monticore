/* (c) https://github.com/MontiCore/monticore */
import automaton3._ast.ASTInvariant;
import automaton3._visitor.Automaton3DelegatorVisitor;

/**
 * 
 */
public class Automaton3ComposedVisit extends Automaton3DelegatorVisitor {

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

