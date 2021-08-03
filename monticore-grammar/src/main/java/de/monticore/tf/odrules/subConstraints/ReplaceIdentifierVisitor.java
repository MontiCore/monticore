/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.tf.odrules._ast.ASTAssignment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * This visitor replaces identifier variables with a call to their real variables.
 *
 * An identifier variable is a short way to reference the name of an element in a transformation.
 * This identifier does not have any value before it is assigned in later stages of the transformation.
 * In order to read the identifier variable early on in the transformation we have to replace it.
 */
public class ReplaceIdentifierVisitor implements
        CommonExpressionsVisitor2,
        ExpressionsBasisVisitor2 {

  protected Map<String, String> assignments;
  protected List<ASTAssignment> assignmentsAsAST;

  public ReplaceIdentifierVisitor(List<ASTAssignment> assignmentsAsAST){
    super();
    this.assignmentsAsAST = assignmentsAsAST;
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getExpression());
      if(assignment.isPresent()) {
        node.setExpression(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTBooleanNotExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getExpression());
      if(assignment.isPresent()) {
        node.setExpression(assignment.get().getRhs());
      }
    }
  }


  @Override
  public void visit(ASTLogicalNotExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getExpression());
      if(assignment.isPresent()) {
        node.setExpression(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTEqualsExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getLeft());
      if(assignment.isPresent()) {
        node.setLeft(assignment.get().getRhs());
      }
    }
    if (node.getRight() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getRight());
      if(assignment.isPresent()) {
        node.setRight(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getLeft());
      if(assignment.isPresent()) {
        node.setLeft(assignment.get().getRhs());
      }
    }
    if (node.getRight() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getRight());
      if(assignment.isPresent()) {
        node.setRight(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getLeft());
      if(assignment.isPresent()) {
        node.setLeft(assignment.get().getRhs());
      }
    }
    if (node.getRight() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getRight());
      if(assignment.isPresent()) {
        node.setRight(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTPlusExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getLeft());
      if(assignment.isPresent()) {
        node.setLeft(assignment.get().getRhs());
      }
    }
    if (node.getRight() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getRight());
      if(assignment.isPresent()) {
        node.setRight(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTBracketExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getExpression());
      if(assignment.isPresent()) {
        node.setExpression(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTCallExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)node.getExpression());
      if(assignment.isPresent()) {
        node.setExpression(assignment.get().getRhs());
      }
    }
  }

  @Override
  public void visit(ASTArguments node) {
    int index = 0;
    for(ASTExpression expr : node.getExpressionList()){
      if (expr instanceof ASTNameExpression) {
        Optional<ASTAssignment> assignment = checkAssignment((ASTNameExpression)expr);
        if(assignment.isPresent()) {
          node.setExpression(index, assignment.get().getRhs());
        }
      }
      index++;
    }
  }



  private Optional<ASTAssignment> checkAssignment(ASTNameExpression node) {
    for (ASTAssignment a : assignmentsAsAST) {
      if (a.getLhs().equals(node.getName())) {
        return Optional.of(a);
      }
    }
    return Optional.empty();
  }


}
