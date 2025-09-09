/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTBasicFloatLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class BasicFloatLiteralRangeCoCo implements MCCommonLiteralsASTBasicFloatLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for BasicFloatLiteral";
  public static final String ERROR_CODE = "0xA0213";

  protected BigDecimal min;
  protected BigDecimal max;

  public BasicFloatLiteralRangeCoCo(){
    this.min = BigDecimal.valueOf(-Float.MAX_VALUE);
    this.max = BigDecimal.valueOf(Float.MAX_VALUE);
  }

  public BasicFloatLiteralRangeCoCo(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTBasicFloatLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource().substring(0, node.getSource().length()-1));
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
