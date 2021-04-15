/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageTraverser;

import java.util.Optional;

public class DeriveSymTypeOfMyOwnLanguage
    implements IDerive {

  private MyOwnLanguageTraverser traverser;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();

  public DeriveSymTypeOfMyOwnLanguage(){
    init();
  }

  @Override
  public MyOwnLanguageTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MyOwnLanguageTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }

  @Override
  public void init() {
    traverser = MyOwnLanguageMill.traverser();
    typeCheckResult = new TypeCheckResult();
    DeriveSymTypeOfCommonExpressions ce = new DeriveSymTypeOfCommonExpressions();
    ce.setTypeCheckResult(typeCheckResult);
    traverser.add4CommonExpressions(ce);
    traverser.setCommonExpressionsHandler(ce);

    DeriveSymTypeOfExpression eb = new DeriveSymTypeOfExpression();
    eb.setTypeCheckResult(typeCheckResult);
    traverser.add4ExpressionsBasis(eb);
    traverser.setExpressionsBasisHandler(eb);

    DeriveSymTypeOfMyOwnExpressionGrammar moeg = new DeriveSymTypeOfMyOwnExpressionGrammar();
    moeg.setTypeCheckResult(typeCheckResult);
    traverser.setMyOwnExpressionGrammarHandler(moeg);
    traverser.add4MyOwnExpressionGrammar(moeg);

    DeriveSymTypeOfMCCommonLiterals cl = new DeriveSymTypeOfMCCommonLiterals();
    cl.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCommonLiterals(cl);
  }
}
