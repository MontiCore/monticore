package de.monticore.javalight.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.javalight._visitor.JavaLightVisitor2;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor2;

import java.util.Map;
import java.util.Optional;

public class ReturnStatementCollectionVisitor
    implements JavaLightVisitor2, MCReturnStatementsVisitor2 {
  
  Map<ASTNode, Optional<ASTExpression>> returnExpressionMap;
  
  public ReturnStatementCollectionVisitor(
      Map<ASTNode, Optional<ASTExpression>> returnExpressionList) {
    this.returnExpressionMap = returnExpressionList;
  }
  
  public Map<ASTNode, Optional<ASTExpression>> getReturnExpressions() {
    return this.returnExpressionMap;
  }
  
  @Override
  public void visit(ASTReturnStatement node) {
    if (node.isPresentExpression()) {
      this.returnExpressionMap.put(node, Optional.of(node.getExpression()));
    }
    else {
      this.returnExpressionMap.put(node, Optional.empty());
    }
  }
}
