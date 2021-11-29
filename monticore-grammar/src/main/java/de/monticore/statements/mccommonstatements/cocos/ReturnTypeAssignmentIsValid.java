/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.javalight._visitor.JavaLightVisitor2;
import de.monticore.statements.mcreturnstatements.MCReturnStatementsMill;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsTraverser;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor2;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisTraverser;
import de.monticore.types.check.TypeCheck;

import java.util.List;

public class ReturnTypeAssignmentIsValid implements JavaLightASTMethodDeclarationCoCo {
  
  JavaLightTraverser traverser;
  
  TypeCheck typeCheck;
  
  public ReturnTypeAssignmentIsValid(TypeCheck typeCheck, JavaLightTraverser traverser){
    this.typeCheck = typeCheck;
    this.traverser = traverser;
  }
  
  
  @Override
  public void check(ASTMethodDeclaration node) {
    
    MCReturnStatementsTraverser traverser = MCReturnStatementsMill.traverser();
    JavaReturnStatementCollector returnStatementCollector = new JavaReturnStatementCollector(){};
    traverser.add4MCReturnStatements(returnStatementCollector);
    
    node.accept(traverser);
    
  }
  
  private class JavaReturnStatementCollector implements MCReturnStatementsVisitor2 {
  
    List<ASTReturnStatement> returnStatementList;
    
    private List<ASTReturnStatement> getReturnStatementList(){
      return this.returnStatementList;
    }
    
    @Override
    public void visit(ASTReturnStatement node){
      returnStatementList.add(node);
    }
    
  }
}