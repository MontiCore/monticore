import expression._ast.*;
import expression._visitor.*;

/**
 *
 */
public class ExpressionCheapVisit implements ExpressionVisitor {

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  ExpressionVisitor realThis = this;

  @Override
  public void setRealThis(ExpressionVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionVisitor getRealThis() {
    return realThis;
  }

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
