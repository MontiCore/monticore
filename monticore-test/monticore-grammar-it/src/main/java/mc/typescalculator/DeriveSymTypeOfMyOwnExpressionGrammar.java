/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.AbstractDeriveFromExpression;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;
import mc.typescalculator.myownexpressiongrammar._visitor.MyOwnExpressionGrammarHandler;
import mc.typescalculator.myownexpressiongrammar._visitor.MyOwnExpressionGrammarTraverser;
import mc.typescalculator.myownexpressiongrammar._ast.ASTAbsoluteExpression;
import de.se_rwth.commons.logging.Log;
import mc.typescalculator.myownexpressiongrammar._visitor.MyOwnExpressionGrammarVisitor2;

public class DeriveSymTypeOfMyOwnExpressionGrammar extends AbstractDeriveFromExpression
    implements MyOwnExpressionGrammarVisitor2, MyOwnExpressionGrammarHandler {

  protected MyOwnExpressionGrammarTraverser traverser;

  @Override
  public MyOwnExpressionGrammarTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MyOwnExpressionGrammarTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTAbsoluteExpression expr){
    SymTypeExpression inner = null;
    SymTypeExpression result = null;

    expr.getExpression().accept(getTraverser());
    if(typeCheckResult.isPresentResult()){
      inner = typeCheckResult.getResult();
    }else{
      Log.error("0xB0001 the inner result " +
          "cannot be calculated");
    }

   if(inner.isTypeConstant()
       &&((SymTypeConstant)inner).isNumericType()){
      result = inner.deepClone();
    }

    if(result!=null){
      typeCheckResult.setResult(result);
    }else{
      typeCheckResult.reset();
      Log.error("0xB0003 the result" +
          "cannot be calculated");
    }
  }
}
