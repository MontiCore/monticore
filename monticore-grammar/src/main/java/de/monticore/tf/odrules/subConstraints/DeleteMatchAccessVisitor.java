/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;


import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;

/**
 * This visitor cuts off the 'm.' prefix used for accessing elements in a given Match m.
 * In SubConstraints these elements are accessed by their original name.
 */
public class DeleteMatchAccessVisitor implements
        CommonExpressionsVisitor2 {


  public DeleteMatchAccessVisitor(){
    super();
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (node.getExpression() instanceof ASTFieldAccessExpression faExpression) {
      node.setExpression(replaceNode(faExpression));
    }
  }

  @Override
  public void visit(ASTBooleanNotExpression node) {
    if (node.getExpression() instanceof ASTFieldAccessExpression faExpression) {
      node.setExpression(replaceNode(faExpression));
    }
  }

  @Override
  public void visit(ASTLogicalNotExpression node) {
    if (node.getExpression() instanceof ASTFieldAccessExpression faExpression) {
      node.setExpression(replaceNode(faExpression));
    }
  }

  @Override
  public void visit(ASTEqualsExpression node) {
    if (node.getLeft() instanceof ASTFieldAccessExpression leftFaExpression) {
      node.setLeft(replaceNode(leftFaExpression));
    }
    if (node.getRight() instanceof ASTFieldAccessExpression rightFaExpression) {
      node.setRight(replaceNode(rightFaExpression));
    }
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
    if (node.getLeft() instanceof ASTFieldAccessExpression leftFaExpression) {
      node.setLeft(replaceNode(leftFaExpression));
    }
    if (node.getRight() instanceof ASTFieldAccessExpression rightFaExpression) {
      node.setRight(replaceNode(rightFaExpression));
    }
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    if (node.getLeft() instanceof ASTFieldAccessExpression leftFaExpression) {
      node.setLeft(replaceNode(leftFaExpression));
    }
    if (node.getRight() instanceof ASTFieldAccessExpression rightFaExpression) {
      node.setRight(replaceNode(rightFaExpression));
    }
  }

  @Override
  public void visit(ASTBracketExpression node) {
    if (node.getExpression() instanceof ASTFieldAccessExpression faExpression) {
      node.setExpression(replaceNode(faExpression));
    }
  }

  @Override
  public void visit(ASTPlusExpression node) {
    if (node.getLeft() instanceof ASTFieldAccessExpression leftFaExpression) {
      node.setLeft(replaceNode(leftFaExpression));
    }
    if (node.getRight() instanceof ASTFieldAccessExpression rightFaExpression) {
      node.setRight(replaceNode(rightFaExpression));
    }
  }

  private ASTExpression replaceNode(ASTFieldAccessExpression node) {
    //Look for a child of a child that is a primary expression with name 'm'
    if(node.getExpression() instanceof ASTNameExpression nameExpression) {
      if(nameExpression.getName().equals("m")) {
        return buildNode(node);
      }
    }

    return node;
  }


  private ASTNameExpression buildNode(ASTFieldAccessExpression node) {
    //If found, remove this child and create a new primary node
    ASTNameExpression primExpr = CommonExpressionsMill.nameExpressionBuilder()
            .setName(node.getName())
            .build();

    return primExpr;
  }

}
