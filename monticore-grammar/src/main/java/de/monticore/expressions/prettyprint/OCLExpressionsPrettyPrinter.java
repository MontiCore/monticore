// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.oclexpressions._ast.*;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class OCLExpressionsPrettyPrinter extends ExpressionsBasisPrettyPrinter implements OCLExpressionsVisitor {

  protected OCLExpressionsVisitor realThis;

  public OCLExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    realThis = this;
  }

  @Override
  public void handle(ASTInExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentExtType())
      node.getExtType().accept(getRealThis());

    Iterator iter = node.getVarNameList().iterator();
    getPrinter().print(iter.next());
    while (iter.hasNext()) {
      getPrinter().print(", ");
      getPrinter().print(iter.next());
    }

    if (node.isPresentExpression()) {
      getPrinter().print(" in ");
      node.getExpression().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" implies ");
    node.getRight().accept(getRealThis());
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
  public void handle(ASTSingleLogicalANDExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" & ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTForallExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("forall ");
    node.getInExprsList().forEach(e -> e.accept(getRealThis()));

    getPrinter().print(":");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTExistsExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("exists ");
    node.getInExprsList().forEach(e -> e.accept(getRealThis()));

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
    for (ASTEDeclarationExt ast : node.getEDeclarationsList()) {
      ast.accept(getRealThis());
      getPrinter().print("; ");
    }
    getPrinter().print("in ");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIterateExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("iterate { ");
    node.getIteration().accept(getRealThis());
    getPrinter().print("; ");
    node.getInit().accept(getRealThis());
    getPrinter().print(" : ");
    getPrinter().print(node.getName() + " = ");
    node.getValue().accept(getRealThis());
    getPrinter().print(" }");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTInstanceOfExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" instanceof ");
    node.getExtType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTThenExpressionPart node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(" then ");
    node.getThenExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTElseExpressionPart node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(" else ");
    node.getElseExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIfThenElseExpr node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("if ");
    node.getCondition().accept(getRealThis());
    node.getThenExpressionPart().accept(getRealThis());
    node.getElseExpressionPart().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTParenthizedExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExpression().accept(getRealThis());
    getPrinter().print(")");
    if (node.isPresentQualification()) {
      getPrinter().print(".");
      node.getQualification().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExtType().accept(getRealThis());
    getPrinter().print(")");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExtType().accept(getRealThis());
    getPrinter().print("{");
    if (node.isPresentExpression()) {
      node.getExpression().accept(getRealThis());
    }
    getPrinter().print("}");
    if (node.isPresentQualification()) {
      getPrinter().print(".");
      node.getQualification().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLIsNewPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("isnew(");
    node.getExpression().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLDefinedPrimary node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("defined(");
    node.getExpression().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLArrayQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    for (ASTExpression astExpression : node.getArgumentsList()) {
      getPrinter().print("[");
      astExpression.accept(getRealThis());
      getPrinter().print("]");
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLArgumentQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    for (int i = 0; i < node.getExpressionsList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getExpressions(i).accept(getRealThis());
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
    String qualifiedName = String.join(".", node.getNamesList());
    getPrinter().print(qualifiedName + " ");
    if (node.isPresentPostfix()) {
      node.getPostfix().accept(getRealThis());
    }
    if (node.isPresentOCLQualifiedPrimary()) {
      getPrinter().print(".");
      node.getOCLQualifiedPrimary().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionExpressionStyle node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(" | ");
    for (int i = 0; i < node.getOCLComprehensionItemsList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getOCLComprehensionItems(i).accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionItem node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentGenerator()) {
      node.getGenerator().accept(getRealThis());
    } else if (node.isPresentDeclaration()) {
      node.getDeclaration().accept(getRealThis());
    } else if (node.isPresentFilter()) {
      node.getFilter().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLComprehensionEnumerationStyle node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getOCLCollectionItemsList().forEach(i -> i.accept(getRealThis()));
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLCollectionItem node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if(node.sizeExpressions() == 2){
      node.getExpressions(0).accept(getRealThis());
      getPrinter().print(" .. ");
      node.getExpressions(1).accept(getRealThis());
    }else {
      for (int i = 0; i < node.getExpressionsList().size(); i++) {
        if (i != 0) {
          getPrinter().print(", ");
        }
        node.getExpressions(i).accept(getRealThis());
      }
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTOCLEquivalentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("<=>");
    node.getRight().accept(getRealThis());
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

  public String prettyprint(ASTThenExpressionPart node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTElseExpressionPart node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLQualification node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLComprehensionExpr node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLCollectionItem node) {
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
