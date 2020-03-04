package mc.typescalculator;

import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import mc.typescalculator.myownexpressiongrammar._visitor.MyOwnExpressionGrammarVisitor;
import mc.typescalculator.myownexpressiongrammar._ast.ASTPowerExpression;
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
  public void traverse(ASTPowerExpression expr){
    SymTypeExpression base = null;
    SymTypeExpression exponent = null;
    SymTypeExpression result = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      base = lastResult.getLast();
    }else{
      Log.error("0xB0001 the left result " +
          "cannot be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      exponent = lastResult.getLast();
    }else{
      Log.error("0xB0002 the right result" +
          "cannot be calculated");
    }

    //assuming there are only int and double
    //there are more possibilities with float, long, char,...
    if("double".equals(base.print())){
      if("int".equals(exponent.print())
          || "double".equals(exponent.print())) {
        result = SymTypeExpressionFactory
            .createTypeConstant("double");
      }
    }

    if("int".equals(base.print())){
      if("double".equals(exponent.print())){
        result = SymTypeExpressionFactory
            .createTypeConstant("double");
      }else if("int".equals(exponent.print())){
        result = SymTypeExpressionFactory
            .createTypeConstant("int");
      }
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
