/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals.cocos;

import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsASTFloatLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class FloatLiteralRange implements MCJavaLiteralsASTFloatLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for FloatLiteral";
  public static final String ERROR_CODE = "0xA0219";

  protected BigDecimal min;
  protected BigDecimal max;

  public FloatLiteralRange(){
    this.min = BigDecimal.valueOf(-Float.MAX_VALUE);
    this.max = BigDecimal.valueOf(Float.MAX_VALUE);
  }

  public FloatLiteralRange(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTFloatLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource().substring(0, node.getSource().length()-1));
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
