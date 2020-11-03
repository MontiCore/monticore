/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageDelegatorVisitor;

import java.util.Optional;

public class DeriveSymTypeOfMyOwnLanguage
    extends MyOwnLanguageDelegatorVisitor
    implements ITypesCalculator {

  private MyOwnLanguageDelegatorVisitor realThis;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();

  public DeriveSymTypeOfMyOwnLanguage(){
    this.realThis = this;
    init();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    ex.accept(realThis);
    return Optional.of(typeCheckResult.getCurrentResult());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return Optional.of(typeCheckResult.getCurrentResult());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    lit.accept(realThis);
    return Optional.of(typeCheckResult.getCurrentResult());
  }

  @Override
  public void init() {
    typeCheckResult = new TypeCheckResult();
    DeriveSymTypeOfCommonExpressions ce = new DeriveSymTypeOfCommonExpressions();
    ce.setTypeCheckResult(typeCheckResult);
    setCommonExpressionsVisitor(ce);
    DeriveSymTypeOfExpression eb = new DeriveSymTypeOfExpression();
    eb.setTypeCheckResult(typeCheckResult);
    setExpressionsBasisVisitor(eb);
    DeriveSymTypeOfMyOwnExpressionGrammar moeg = new DeriveSymTypeOfMyOwnExpressionGrammar();
    moeg.setTypeCheckResult(typeCheckResult);
    setMyOwnExpressionGrammarVisitor(moeg);
    DeriveSymTypeOfMCCommonLiterals cl = new DeriveSymTypeOfMCCommonLiterals();
    cl.setTypeCheckResult(typeCheckResult);
    setMCCommonLiteralsVisitor(cl);
  }
}
