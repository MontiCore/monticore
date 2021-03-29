/* (c) https://github.com/MontiCore/monticore */
package mylang;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mylang._visitor.MyLangTraverser;

import java.util.Optional;

public class DeriveFromMyLang implements IDerive {
  
  protected MyLangTraverser traverser;
  protected TypeCheckResult typeCheckResult;
  
  
  @Override public void init() {
    traverser = MyLangMill.traverser();
    this.typeCheckResult = new TypeCheckResult();
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
  
  @Override public MyLangTraverser getTraverser() {
    return traverser;
  }
  
  @Override public SymTypeExpression getResult(){
    return typeCheckResult.isPresentCurrentResult() ? Optional.ofNullable(typeCheckResult.getCurrentResult()) : Optional.empty();
  }
}
