/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types3.TypeCheck3;

public class MCCommonStatementsSymTabCompletion implements MCCommonStatementsVisitor2 {

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclarator().getSymbol();
    symbol.setType(TypeCheck3.symTypeFromAST(ast.getMCType()));
  }
}
