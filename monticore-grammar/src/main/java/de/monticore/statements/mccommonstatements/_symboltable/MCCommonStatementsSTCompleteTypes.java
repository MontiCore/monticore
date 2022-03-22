/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCFGT4Grammar;
import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class MCCommonStatementsSTCompleteTypes implements MCCommonStatementsVisitor2 {

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  protected SymTypeExpression createTypeLoader(ASTMCType ast) {
    FullSynthesizeFromMCFGT4Grammar synFromFull = new FullSynthesizeFromMCFGT4Grammar();
    // Start visitor
    TypeCheckResult typeCheckResult = synFromFull.synthesizeType(ast);
    if(typeCheckResult.isPresentCurrentResult()){
      return typeCheckResult.getCurrentResult();
    }
    return new SymTypeOfNull();
  }

}
