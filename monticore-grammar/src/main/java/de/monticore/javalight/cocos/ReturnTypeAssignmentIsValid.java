/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class ReturnTypeAssignmentIsValid implements JavaLightASTMethodDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0910 ";
  
  public static final String ERROR_MSG_FORMAT = "Return statements of void methods must all be empty.";
  
  public static final String ERROR_CODE_2 = "0xA0911 ";
  
  public static final String ERROR_MSG_FORMAT_2 = "Return statements of non void methods must not be empty.";
  
  public static final String ERROR_CODE_3 = "0xA0912 ";
  
  public static final String ERROR_MSG_FORMAT_3 = "Return statement must be of the type of the method or a subtype of it.";

  public ReturnTypeAssignmentIsValid() {}
  
  @Override
  public void check(ASTMethodDeclaration node) {
    Map<ASTNode, Optional<ASTExpression>> returnStatements = getReturnExpressions(node);

    SymTypeExpression typeOfMethod = TypeCheck3.symTypeFromAST(node.getMCReturnType());

    // Check return-Statements
    if (node.isPresentMCJavaBlock()) {
      if (typeOfMethod.isVoidType()) {
        for (Map.Entry<ASTNode, Optional<ASTExpression>> entry : returnStatements.entrySet()) {
          if (entry.getValue().isPresent()) {
            Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
          }
        }
      }
      if (!typeOfMethod.isVoidType() && returnStatements.isEmpty()) {
        Log.error(ERROR_CODE_2 + ERROR_MSG_FORMAT_2, node.get_SourcePositionStart());
      }
      if (!typeOfMethod.isVoidType() && !returnStatements.isEmpty()) {
        for (Map.Entry<ASTNode, Optional<ASTExpression>> entry : returnStatements.entrySet()) {
          if (entry.getValue().isEmpty()) {
            Log.error(ERROR_CODE_2 + ERROR_MSG_FORMAT_2, node.get_SourcePositionStart());
          } else {
            SymTypeExpression returnType = TypeCheck3.typeOf(entry.getValue().get(), typeOfMethod);
            if (!SymTypeRelations.isCompatible(typeOfMethod, returnType)) {
              Log.error(ERROR_CODE_3 + ERROR_MSG_FORMAT_3, node.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }

  /**
   * Collects all return statements from the given AST node using a traverser.
   * <p>
   * This method extracts return statements and their associated expressions
   * from the method body. It returns a map where each return statement node
   * is mapped to an Optional containing either the return expression or an empty
   * Optional for return statements without an expression.
   * <p>
   * <b>Note:</b> For languages with nested scopes that allow their own return statements
   * (e.g., lambda expressions, anonymous classes, or inner class methods),
   * this method may need to be overridden to handle scope boundaries correctly.
   * See {@link ReturnStatementCollectionVisitor} for more information.
   *
   * @param node the AST node (typically a method declaration) to traverse for return statements
   * @return a map associating each return statement with its optional expression
   */
  protected Map<ASTNode, Optional<ASTExpression>> getReturnExpressions(ASTNode node) {
    JavaLightTraverser traverser = JavaLightMill.inheritanceTraverser();
    Map<ASTNode, Optional<ASTExpression>> returnExpressions = new HashMap<>();
    ReturnStatementCollectionVisitor returnStatementCollectionVisitor = new ReturnStatementCollectionVisitor(returnExpressions);
    traverser.add4MCReturnStatements(returnStatementCollectionVisitor);
    node.accept(traverser);
    return returnStatementCollectionVisitor.getReturnExpressions();
  }
}
