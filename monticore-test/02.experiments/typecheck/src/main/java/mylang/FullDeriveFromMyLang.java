/* (c) https://github.com/MontiCore/monticore */
package mylang;

import de.monticore.types.check.*;
import mylang._visitor.MyLangTraverser;

public class FullDeriveFromMyLang extends AbstractDerive {
  
  public FullDeriveFromMyLang(){
    this(MyLangMill.traverser());
  }

  public FullDeriveFromMyLang(MyLangTraverser traverser){
    super(traverser);
    init(traverser);
  }
  
  
  public void init(MyLangTraverser traverser) {
    DeriveSymTypeOfExpression expBasis = new DeriveSymTypeOfExpression();
    expBasis.setTypeCheckResult(typeCheckResult);
    traverser.add4ExpressionsBasis(expBasis);
    traverser.setExpressionsBasisHandler(expBasis);
    DeriveSymTypeOfCommonExpressions commonexp = new DeriveSymTypeOfCommonExpressions();
    commonexp.setTypeCheckResult(typeCheckResult);
    traverser.add4CommonExpressions(commonexp);
    traverser.setCommonExpressionsHandler(commonexp);
    DeriveSymTypeOfLiterals literals = new DeriveSymTypeOfLiterals();
    literals.setTypeCheckResult(typeCheckResult);
    traverser.add4MCLiteralsBasis(literals);
    DeriveSymTypeOfMCCommonLiterals commonliterals = new DeriveSymTypeOfMCCommonLiterals();
    commonliterals.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCommonLiterals(commonliterals);
  }

}
