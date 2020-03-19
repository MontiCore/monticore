package mc.typescalculator;

import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;
import mc.typescalculator.myownexpressiongrammar._visitor.MyOwnExpressionGrammarVisitor;
import mc.typescalculator.myownexpressiongrammar._ast.ASTAbsoluteExpression;
import de.se_rwth.commons.logging.Log;

public class DeriveSymTypeOfMyOwnExpressionGrammar
    extends DeriveSymTypeOfExpression
    implements MyOwnExpressionGrammarVisitor {

  protected MyOwnExpressionGrammarVisitor realThis;

  public void setRealThis(MyOwnExpressionGrammarVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MyOwnExpressionGrammarVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void traverse(ASTAbsoluteExpression expr){
    SymTypeExpression inner = null;
    SymTypeExpression result = null;

    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      inner = lastResult.getLast();
    }else{
      Log.error("0xB0001 the inner result " +
          "cannot be calculated");
    }

    //absolute amount is only possible for numeric types
   if(inner.isPrimitive()
       &&((SymTypeConstant)inner).isNumericType()){
      result = inner.deepClone();
    }

    if(result!=null){
      lastResult.setLast(result);
    }else{
      lastResult.reset();
      Log.error("0xB0003 the result" +
          "cannot be calculated");
    }
  }
}
