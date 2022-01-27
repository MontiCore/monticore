/* (c) https://github.com/MontiCore/monticore */
package automata3;
import expression._ast.*;
import expression._visitor.*;

/**
 * Visitor for the expression language. 
 * 
 * But the visitor contains only the content of the elements defined on
 * Automata3 level. Nodes from super languages must be visited by their
 * respective visitors providing the corresponding visit and endVisit 
 * methods.
 */
public class ExpressionVisit implements ExpressionVisitor2 {

  // ----------------------------------------------------------
  // Typical visit/endvist methods:
  
  @Override
  public void visit(ASTTruth node) {
    System.out.println(" ASTTruth " + node.isTt()+ "," + node.isFf()); 
  }
  
  @Override
  public void visit(ASTNot node) {
    System.out.println(" ASTNot ");
  }
  
  @Override
  public void visit(ASTVariable node) {
    System.out.println(" ASTVariable " + node.getName());
  }
  
  @Override
  public void visit(ASTAnd node) {
    System.out.println(" ASTAnd ");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTTruth node) {
    System.out.println("   /Truth");
  }

  @Override
  public void endVisit(ASTNot node) {
    System.out.println("   /Not ");
  }

  @Override
  public void endVisit(ASTVariable node) {
    System.out.println("   /Variable ");
  }

  @Override
  public void endVisit(ASTAnd node) {
    System.out.println("   /And ");
  }
  
}
