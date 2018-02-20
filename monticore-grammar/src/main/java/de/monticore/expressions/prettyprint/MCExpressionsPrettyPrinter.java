/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import java.util.Iterator;

import de.monticore.mcexpressions._ast.ASTAddExpression;
import de.monticore.mcexpressions._ast.ASTArguments;
import de.monticore.mcexpressions._ast.ASTArrayExpression;
import de.monticore.mcexpressions._ast.ASTAssignmentExpression;
import de.monticore.mcexpressions._ast.ASTBinaryAndOpExpression;
import de.monticore.mcexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.mcexpressions._ast.ASTBinaryXorOpExpression;
import de.monticore.mcexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.mcexpressions._ast.ASTBooleanNotExpression;
import de.monticore.mcexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.mcexpressions._ast.ASTBracketExpression;
import de.monticore.mcexpressions._ast.ASTCallExpression;
import de.monticore.mcexpressions._ast.ASTClassExpression;
import de.monticore.mcexpressions._ast.ASTComparisonExpression;
import de.monticore.mcexpressions._ast.ASTConditionalExpression;
import de.monticore.mcexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.mcexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.mcexpressions._ast.ASTIdentityExpression;
import de.monticore.mcexpressions._ast.ASTInstanceofExpression;
import de.monticore.mcexpressions._ast.ASTLiteralExpression;
import de.monticore.mcexpressions._ast.ASTLogicalNotExpression;
import de.monticore.mcexpressions._ast.ASTMCExpressionsNode;
import de.monticore.mcexpressions._ast.ASTMultExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._ast.ASTPrefixExpression;
import de.monticore.mcexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.mcexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.mcexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.mcexpressions._ast.ASTShiftExpression;
import de.monticore.mcexpressions._ast.ASTSuffixExpression;
import de.monticore.mcexpressions._ast.ASTSuperExpression;
import de.monticore.mcexpressions._ast.ASTSuperSuffix;
import de.monticore.mcexpressions._ast.ASTThisExpression;
import de.monticore.mcexpressions._ast.ASTTypeCastExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

public class MCExpressionsPrettyPrinter extends TypesPrettyPrinterConcreteVisitor implements MCExpressionsVisitor {

  private boolean WRITE_COMMENTS = false;
  
  private MCExpressionsVisitor realThis = this;

  public MCExpressionsPrettyPrinter(IndentPrinter out) {
    super(out);
    setWriteComments(true);
  }

  protected void printDimensions(int dims) {
    for (int i = 0; i < dims; i++) {
      getPrinter().print("[]");
    }
  }

  @Override
  public void handle(ASTBracketExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    a.getExpression().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTPrimaryThisExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("this");   
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTPrimarySuperExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("super");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLiteralExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLiteral().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTNameExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getName());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTClassExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getReturnType().accept(getRealThis());
    getPrinter().print(".class");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTPrimaryGenericInvocationExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getTypeArguments().accept(getRealThis());
    a.getGenericInvocationSuffix().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTQualifiedNameExpression)
   */
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    printNode(node.getName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTThisExpression)
   */
  @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".this ");
    CommentPrettyPrinter.printPostComments(node, getPrinter());

  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuperExpression)
   */
  @Override
  public void handle(ASTSuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".super ");
    node.getSuperSuffix().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTGenericInvocationExpression)
   */
  @Override
  public void handle(ASTGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    node.getPrimaryGenericInvocationExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTArrayExpression)
   */
  @Override
  public void handle(ASTArrayExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("[");
    node.getIndexExpression().accept(getRealThis());
    getPrinter().print("]");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTCallExpression)
   */
  @Override
  public void handle(ASTCallExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    node.getArguments().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTTypeCastExpression)
   */
  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getType().accept(getRealThis());
    getPrinter().print(")");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuffixExpression)
   */
  @Override
  public void handle(ASTSuffixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(node.getSuffixOpOpt().orElse(""));
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTPrefixExpression)
   */
  @Override
  public void handle(ASTPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getPrefixOpOpt().orElse(""));
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanNotExpression)
   */
  @Override
  public void handle(ASTBooleanNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("~");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTLogicalNotExpression)
   */
  @Override
  public void handle(ASTLogicalNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("!");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTMultExpression)
   */
  @Override
  public void handle(ASTMultExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getMultiplicativeOpOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTAddExpression)
   */
  @Override
  public void handle(ASTAddExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getAdditiveOpOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTShiftExpression)
   */
  @Override
  public void handle(ASTShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getShiftOpOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTComparisonExpression)
   */
  @Override
  public void handle(ASTComparisonExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getComparisonOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTInstanceofExpression)
   */
  @Override
  public void handle(ASTInstanceofExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(" instanceof ");
    node.getType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTIdentityExpression)
   */
  @Override
  public void handle(ASTIdentityExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getIdentityTestOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryAndOpExpression)
   */
  @Override
  public void handle(ASTBinaryAndOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("&");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryXorOpExpression)
   */
  @Override
  public void handle(ASTBinaryXorOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("^");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryOrOpExpression)
   */
  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("|");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanAndOpExpression)
   */
  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("&&");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanOrOpExpression)
   */
  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("||");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTConditionalExpression)
   */
  @Override
  public void handle(ASTConditionalExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getCondition().accept(getRealThis());
    getPrinter().print("?");
    node.getTrueExpression().accept(getRealThis());
    getPrinter().print(": ");
    node.getFalseExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTAssignmentExpression)
   */
  @Override
  public void handle(ASTAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getAssignmentOpt().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTGenericInvocationSuffix)
   */
  @Override
  public void handle(ASTGenericInvocationSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isSuper()) {
      getPrinter().print(" super ");
      node.getSuperSuffix().accept(getRealThis());
    }
    if (node.isThis()) {
      getPrinter().print(" this ");
      node.getSuperSuffix().accept(getRealThis());
    }
    if (node.isPresentName()) {
      printNode(node.getName());
      node.getArguments().accept(getRealThis());    
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuperSuffix)
   */
  @Override
  public void handle(ASTSuperSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentName()) {
      getPrinter().print(".");
      if (node.isPresentTypeArguments()) {
        node.getTypeArguments().accept(getRealThis());
      }
      printNode(node.getName());
    }
    if (node.isPresentArguments()) {
      node.getArguments().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }


  @Override
  public void handle(ASTArguments a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    printExpressionsList(a.getExpressionList().iterator(), ", ");
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  protected void printNode(String s) {
    getPrinter().print(s);
  }

  protected void printExpressionsList(Iterator<? extends ASTMCExpressionsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  public void setWriteComments(boolean wc) {
    WRITE_COMMENTS = wc;
  }

  public boolean isWriteCommentsEnabeled() {
    return WRITE_COMMENTS;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCExpressionsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExpressionsVisitor getRealThis() {
    return realThis;
  }
  
  
}
