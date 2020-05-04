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

  private TypeCheckResult typeCheckResult = new TypeCheckResult();

  public DeriveSymTypeOfMyOwnLanguage(){
    this.realThis = this;
    init();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    ex.accept(realThis);
    return Optional.of(typeCheckResult.getLast());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return Optional.of(typeCheckResult.getLast());
  }

  @Override
  public void init() {
    typeCheckResult = new TypeCheckResult();
    TypeSymbolsScope scope = TypeSymbolsSymTabMill.typeSymbolsScopeBuilder().build();
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setScope(scope);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setScope(scope);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);
    deriveSymTypeOfMyOwnExpressionGrammar = new DeriveSymTypeOfMyOwnExpressionGrammar();
    deriveSymTypeOfMyOwnExpressionGrammar.setTypeCheckResult(typeCheckResult);
    setMyOwnExpressionGrammarVisitor(deriveSymTypeOfMyOwnExpressionGrammar);
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setResult(typeCheckResult);
    setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
  }
}
