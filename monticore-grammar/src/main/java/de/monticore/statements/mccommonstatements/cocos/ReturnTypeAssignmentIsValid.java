/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor2;
import de.monticore.types.check.TypeCheck;

import java.util.List;

public class ReturnTypeAssignmentIsValid implements JavaLightASTMethodDeclarationCoCo {

  TypeCheck typeCheck;
  
  public ReturnTypeAssignmentIsValid(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  
  @Override
  public void check(ASTMethodDeclaration node) {
    // Collect return-statments
    JavaLightTraverser traverser = JavaLightMill.traverser();
    JavaReturnStatementCollector returnStatementCollector = new JavaReturnStatementCollector(){};
    traverser.add4MCReturnStatements(returnStatementCollector);
    node.accept(traverser);
    List<ASTReturnStatement> retStatements = returnStatementCollector.getReturnStatementList();

    // Check return-Statements
    // todo
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