package automata3;
/* (c) https://github.com/MontiCore/monticore */
import automata3._visitor.Automata3Handler;
import automata3._visitor.Automata3Traverser;

/**
 * Handler for the composed language Automata3. 
 * 
 * But the handler is an optional construct to provide 
 * customized handle and traversal strategies for a 
 * composed visitor (i.e., the languages traverser).
 */
public class Automata3Handle implements Automata3Handler {
  
  Automata3Traverser traverser;
  
  public Automata3Traverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(Automata3Traverser traverser) {
    this.traverser = traverser;
  }
  
  // ----------------------------------------------------------
  // Typical handle/traverse methods:
  
  public void handle(automata3._ast.ASTInvariant node) {
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  public void traverse(automata3._ast.ASTInvariant node) {
    if (null != node.getLogicExpr()) {
      node.getLogicExpr().accept(getTraverser());
    }
  }
  
}
