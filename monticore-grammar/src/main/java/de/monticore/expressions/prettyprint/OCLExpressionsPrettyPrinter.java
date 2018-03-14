/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.oclexpressions._ast.ASTAnyExpr;
import de.monticore.oclexpressions._ast.ASTExistsExpr;
import de.monticore.oclexpressions._ast.ASTForallExpr;
import de.monticore.oclexpressions._ast.ASTImpliesExpression;
import de.monticore.oclexpressions._ast.ASTIterateExpr;
import de.monticore.oclexpressions._ast.ASTLetDeclaration;
import de.monticore.oclexpressions._ast.ASTLetinExpr;
import de.monticore.oclexpressions._ast.ASTEDeclarationExt;
import de.monticore.oclexpressions._ast.ASTSingleLogicalORExpr;
import de.monticore.oclexpressions._ast.ASTInExpr;
import de.monticore.oclexpressions._visitor.OCLExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

/**
 * @author npichler
 */

public class OCLExpressionsPrettyPrinter implements OCLExpressionsVisitor {
  
 protected OCLExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  
  public OCLExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
  }

    @Override
    public void handle(ASTInExpr node) {
        CommentPrettyPrinter.printPreComments(node, getPrinter());
        if(node.isPresentType())
            node.getType().accept(getRealThis());

        Iterator iter = node.getVarNameList().iterator();
        getPrinter().print(iter.next());
        while (iter.hasNext()) {
            getPrinter().print(", ");
            getPrinter().print(iter.next());
        }

        if(node.isPresentExpression()) {
            getPrinter().print(" in ");
            node.getExpression().accept(getRealThis());
        }
        CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  @Override
  public void handle(ASTImpliesExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
     getPrinter().print(" implies ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSingleLogicalORExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
     getPrinter().print(" | ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTForallExpr node) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      getPrinter().print("forall ");
      node.getInExprList().forEach(e -> e.accept(getRealThis()));

      getPrinter().print(":");
      node.getExpression().accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTExistsExpr node) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      getPrinter().print("exists ");
      node.getInExprList().forEach(e -> e.accept(getRealThis()));

      getPrinter().print(":");
      node.getExpression().accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTAnyExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
     getPrinter().print("any ");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLetinExpr node) {
     CommentPrettyPrinter.printPreComments(node, getPrinter());
     getPrinter().print("let ");
     for (ASTEDeclarationExt ast : node.getDeclarationList()) {
        ast.accept(getRealThis());
        getPrinter().print("; ");
     }
     getPrinter().print("in ");
     node.getExpression().accept(getRealThis());
     CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLetDeclaration node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
     getPrinter().print("let ");
    for (ASTEDeclarationExt ast : node.getDeclarationList()) {
      ast.accept(getRealThis());
       getPrinter().print(";");
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTIterateExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
     getPrinter().print("iterate { ");
    node.getIterationDeclarator().accept(getRealThis());
     getPrinter().print("; ");
    node.getInitDeclarator().accept(getRealThis());
     getPrinter().print(" : ");
     getPrinter().print(node.getAccumulatorName() + " = ");
    node.getAccumulatorValue().accept(getRealThis());
     getPrinter().print(" }");
     CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  

  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
  @Override
  public void setRealThis(OCLExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public OCLExpressionsVisitor getRealThis() {
    return realThis;
  }
  
}
