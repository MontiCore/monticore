/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mcvardeclarationstatements._symboltable;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCSGT4Grammar;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

public class MCVarDeclarationStatementsSTCompleteTypes implements MCVarDeclarationStatementsVisitor2 {

  protected ISynthesize typeSynthesizer;

  public MCVarDeclarationStatementsSTCompleteTypes() {
    this(new FullSynthesizeFromMCSGT4Grammar());
  }

  public MCVarDeclarationStatementsSTCompleteTypes(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  public ISynthesize getTypeSynthesizer() {
    return this.typeSynthesizer;
  }

  public void endVisit(ASTLocalVariableDeclaration ast) {
    List<FieldSymbol> symbols = Lists.newArrayList();
    for (ASTVariableDeclarator v : ast.getVariableDeclaratorList()) {
      SymTypeExpression simpleType = createTypeLoader(ast.getMCType());
      v.getDeclarator().getSymbol().setType(simpleType);
      symbols.add(v.getDeclarator().getSymbol());
    }
  }

  protected SymTypeExpression createTypeLoader(ASTMCType ast) {
    // Start visitor
    TypeCheckResult typeCheckResult = this.getTypeSynthesizer().synthesizeType(ast);
    if(typeCheckResult.isPresentResult()){
      return typeCheckResult.getResult();
    }
    return new SymTypeOfNull();
  }


}
