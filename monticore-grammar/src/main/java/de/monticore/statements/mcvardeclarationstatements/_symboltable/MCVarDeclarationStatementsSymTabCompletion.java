/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mcvardeclarationstatements._symboltable;

import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;
import de.monticore.types3.TypeCheck3;

public class MCVarDeclarationStatementsSymTabCompletion implements MCVarDeclarationStatementsVisitor2 {

  public MCVarDeclarationStatementsSymTabCompletion() { }

  public void endVisit(ASTLocalVariableDeclaration ast) {
    for (ASTVariableDeclarator v : ast.getVariableDeclaratorList()) {
      v.getDeclarator().getSymbol().setType(TypeCheck3.symTypeFromAST(ast.getMCType()));
    }
  }
}
