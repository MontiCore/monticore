// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.oclexpressions._ast.*;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsHandler;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class OCLExpressionsPrettyPrinter implements OCLExpressionsVisitor2, OCLExpressionsHandler {

  protected OCLExpressionsTraverser traverser;
  
  protected IndentPrinter printer;

  public OCLExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public OCLExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTInExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentExtType())
      node.getExtType().accept(getTraverser());

    Iterator iter = node.getVarNameList().iterator();
    getPrinter().print(iter.next());
    while (iter.hasNext()) {
      getPrinter().print(", ");
      getPrinter().print(iter.next());
    }

    if (node.isPresentExpression()) {
      getPrinter().print(" in ");
      node.getExpression().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" implies ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSingleLogicalORExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" | ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSingleLogicalANDExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" & ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTForallExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("forall ");
    node.getInExprList().forEach(e -> e.accept(getTraverser()));

    getPrinter().print(":");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTExistsExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("exists ");
    node.getInExprList().forEach(e -> e.accept(getTraverser()));

    getPrinter().print(":");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTAnyExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("any ");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLetinExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("let ");
    for (ASTEDeclarationExt ast : node.getEDeclarationList()) {
      ast.accept(getTraverser());
      getPrinter().print("; ");
    }
    getPrinter().print("in ");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIterateExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("iterate { ");
    node.getIteration().accept(getTraverser());
    getPrinter().print("; ");
    node.getInit().accept(getTraverser());
    getPrinter().print(" : ");
    getPrinter().print(node.getName() + " = ");
    node.getValue().accept(getTraverser());
    getPrinter().print(" }");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTInstanceOfExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" instanceof ");
    node.getExtType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTThenExpressionPart node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(" then ");
    node.getThenExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTElseExpressionPart node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(" else ");
    node.getElseExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIfThenElseExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("if ");
    node.getCondition().accept(getTraverser());
    node.getThenExpressionPart().accept(getTraverser());
    node.getElseExpressionPart().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTParenthizedExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
    if (node.isPresentQualification()) {
      getPrinter().print(".");
      node.getQualification().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExtType().accept(getTraverser());
    getPrinter().print(")");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExtType().accept(getTraverser());
    getPrinter().print("{");
    if (node.isPresentExpression()) {
      node.getExpression().accept(getTraverser());
    }
    getPrinter().print("}");
    if (node.isPresentQualification()) {
      getPrinter().print(".");
      node.getQualification().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLIsNewPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("isnew(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLDefinedPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("defined(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLArrayQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    for (ASTExpression astExpression : node.getArgumentsList()) {
      getPrinter().print("[");
      astExpression.accept(getTraverser());
      getPrinter().print("]");
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLArgumentQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    for (int i = 0; i < node.getExpressionList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getExpression(i).accept(getTraverser());
    }
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLAtPreQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("@pre");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLTransitiveQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("**");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLQualifiedPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    String qualifiedName = String.join(".", node.getNameList());
    getPrinter().print(qualifiedName + " ");
    if (node.isPresentPostfix()) {
      node.getPostfix().accept(getTraverser());
    }
    if (node.isPresentOCLQualifiedPrimary()) {
      getPrinter().print(".");
      node.getOCLQualifiedPrimary().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionExpressionStyle node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(" | ");
    for (int i = 0; i < node.getOCLComprehensionItemList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getOCLComprehensionItem(i).accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionItem node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentGenerator()) {
      node.getGenerator().accept(getTraverser());
    } else if (node.isPresentDeclaration()) {
      node.getDeclaration().accept(getTraverser());
    } else if (node.isPresentFilter()) {
      node.getFilter().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionEnumerationStyle node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getOCLCollectionItemList().forEach(i -> i.accept(getTraverser()));
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLCollectionItem node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if(node.sizeExpressions() == 2){
      node.getExpression(0).accept(getTraverser());
      getPrinter().print(" .. ");
      node.getExpression(1).accept(getTraverser());
    }else {
      for (int i = 0; i < node.getExpressionList().size(); i++) {
        if (i != 0) {
          getPrinter().print(", ");
        }
        node.getExpression(i).accept(getTraverser());
      }
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLEquivalentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("<=>");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}
