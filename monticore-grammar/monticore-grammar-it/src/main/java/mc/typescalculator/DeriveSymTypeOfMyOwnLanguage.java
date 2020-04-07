/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageDelegatorVisitor;

import java.util.Optional;

public class DeriveSymTypeOfMyOwnLanguage
    extends MyOwnLanguageDelegatorVisitor
    implements ITypesCalculator {

  private MyOwnLanguageDelegatorVisitor realThis;

  private DeriveSymTypeOfMyOwnExpressionGrammar deriveSymTypeOfMyOwnExpressionGrammar;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private LastResult lastResult = new LastResult();

  public DeriveSymTypeOfMyOwnLanguage(){
    this.realThis = this;
    init();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    ex.accept(realThis);
    return Optional.of(lastResult.getLast());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return Optional.of(lastResult.getLast());
  }

  @Override
  public void init() {
    lastResult = new LastResult();
    TypeSymbolsScope scope = TypeSymbolsSymTabMill.typeSymbolsScopeBuilder().build();
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    deriveSymTypeOfCommonExpressions.setScope(scope);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setLastResult(lastResult);
    deriveSymTypeOfExpression.setScope(scope);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);
    deriveSymTypeOfMyOwnExpressionGrammar = new DeriveSymTypeOfMyOwnExpressionGrammar();
    deriveSymTypeOfMyOwnExpressionGrammar.setLastResult(lastResult);
    setMyOwnExpressionGrammarVisitor(deriveSymTypeOfMyOwnExpressionGrammar);
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setResult(lastResult);
    setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
  }
}
