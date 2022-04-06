/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ReturnTypeAssignmentIsValid implements JavaLightASTMethodDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0910 ";
  
  public static final String ERROR_MSG_FORMAT = "Return statements of void methods must all be empty.";
  
  public static final String ERROR_CODE_2 = "0xA0911 ";
  
  public static final String ERROR_MSG_FORMAT_2 = "Return statements of non void methods must not be empty.";
  
  public static final String ERROR_CODE_3 = "0xA0912 ";
  
  public static final String ERROR_MSG_FORMAT_3 = "Return statement must be of the type of the method or a subtype of it.";
  
  TypeCalculator typeCheck;
  
  public ReturnTypeAssignmentIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTMethodDeclaration node) {
    
    // Collect return-statements
    JavaLightTraverser traverser = JavaLightMill.traverser();
    JavaReturnStatementCollector returnStatementCollector = new JavaReturnStatementCollector();
    traverser.add4MCReturnStatements(returnStatementCollector);
    node.accept(traverser);
    List<ASTReturnStatement> returnStatements = returnStatementCollector.getReturnStatementList();
    
    SymTypeExpression typeOfMethod = typeCheck.symTypeFromAST(node.getMCReturnType());
    
    // Check return-Statements
    if (node.isPresentMCJavaBlock()) {
      if (TypeCheck.isVoid(typeOfMethod)) {
        for (ASTReturnStatement statement : returnStatements) {
          if (statement.isPresentExpression()) {
            Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
          }
        }
      }
      if (!TypeCheck.isVoid(typeOfMethod) && returnStatements.isEmpty()) {
        Log.error(ERROR_CODE_2 + ERROR_MSG_FORMAT_2, node.get_SourcePositionStart());
      }
      if (!TypeCheck.isVoid(typeOfMethod) && !returnStatements.isEmpty()) {
        for (ASTReturnStatement returnStatement : returnStatements) {
          if (!returnStatement.isPresentExpression()) {
            Log.error(ERROR_CODE_2 + ERROR_MSG_FORMAT_2, node.get_SourcePositionStart());
          } else {
            SymTypeExpression returnType = typeCheck.typeOf(returnStatement.getExpression());
            if (!returnType.deepEquals(typeOfMethod)) {
              Log.error(ERROR_CODE_3 + ERROR_MSG_FORMAT_3, node.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
  
  private class JavaReturnStatementCollector implements MCReturnStatementsVisitor2 {
    
    List<ASTReturnStatement> returnStatementList = new ArrayList<>();
    
    private List<ASTReturnStatement> getReturnStatementList() {
      return this.returnStatementList;
    }
    
    @Override
    public void visit(ASTReturnStatement node) {
      returnStatementList.add(node);
    }
    
  }
}
