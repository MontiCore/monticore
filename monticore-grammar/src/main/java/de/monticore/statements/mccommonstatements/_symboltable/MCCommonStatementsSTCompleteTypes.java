package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.FullSynthesizeFromMCFullGenericTypes;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class MCCommonStatementsSTCompleteTypes implements MCCommonStatementsVisitor2 {

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    FullSynthesizeFromMCFullGenericTypes synFromFull = new FullSynthesizeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(synFromFull.getTraverser());
    return synFromFull.getResult().orElse(new SymTypeOfNull());
  }

}
