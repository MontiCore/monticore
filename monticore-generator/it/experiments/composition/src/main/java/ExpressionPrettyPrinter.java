/* (c) Monticore license: https://github.com/MontiCore/monticore */
import de.monticore.prettyprint.IndentPrinter;
import expression._ast.*;
import expression._visitor.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTExpression)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *
 */
public class ExpressionPrettyPrinter implements ExpressionVisitor {

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
  protected IndentPrinter out;

  public ExpressionPrettyPrinter(IndentPrinter o) {
    out = o;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTTruth node) {
    if(node.isTt()) {
      out.print("true ");
    } else {
      out.print("false ");
    }
  }
  
  @Override
  public void visit(ASTNot node) {
    out.print("!");
  }
  
  @Override
  public void visit(ASTVariable node) {
    out.print(node.getName() +" ");
  }
  
  /* 
   * for InfixOps the traversal strategy has to be adapted:
   * The whole node is handled here 
   * (classic traverse, visit, endvisit are out of business) 
   */ 
  public void handle(ASTAnd node) {
    // out.print("/*(*/ ");
    node.getLeft().accept(getRealThis());
    out.print("&& ");
    node.getRight().accept(getRealThis());
    // out.print("/*)*/ ");
  }

}
