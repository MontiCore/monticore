/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.myownlanguage._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTPlusExpressionCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.DeriveSymTypeOfMyOwnLanguage;

public class PlusExpressionReturnsInt implements CommonExpressionsASTPlusExpressionCoCo {

  public static final String ERROR_CODE = "0xB0302";

  public static final String ERROR_MSG_FORMAT = " the result of every PlusExpression needs to be int, not %s";

  @Override
  public void check(ASTPlusExpression node) {
    TypeCheck typeCheck = new TypeCheck(null, new DeriveSymTypeOfMyOwnLanguage());
    SymTypeExpression result = typeCheck.typeOf(node);
    if(!TypeCheck.isInt(result)){
      Log.error(String.format(ERROR_CODE+ERROR_MSG_FORMAT,result.print()));
    }
  }
}
