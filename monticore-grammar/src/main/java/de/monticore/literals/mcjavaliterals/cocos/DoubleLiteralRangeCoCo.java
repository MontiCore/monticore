/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals.cocos;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsASTDoubleLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class DoubleLiteralRangeCoCo implements MCJavaLiteralsASTDoubleLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for DoubleLiteral";
  public static final String ERROR_CODE = "0xA0218";

  protected BigDecimal min;
  protected BigDecimal max;

  public DoubleLiteralRangeCoCo(){
    this.min = BigDecimal.valueOf(-Double.MAX_VALUE);
    this.max = BigDecimal.valueOf(Double.MAX_VALUE);
  }

  public DoubleLiteralRangeCoCo(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTDoubleLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource());
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
