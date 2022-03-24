/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageTraverser;

public class FullDeriveFromMyOwnLanguage
    extends AbstractDerive {

  public FullDeriveFromMyOwnLanguage(){
    this(MyOwnLanguageMill.traverser());
  }

  public FullDeriveFromMyOwnLanguage(MyOwnLanguageTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void setTraverser(MyOwnLanguageTraverser traverser) {
    this.traverser = traverser;
  }

  public void init(MyOwnLanguageTraverser traverser) {
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
