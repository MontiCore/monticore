/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTBasicDoubleLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class BasicDoubleLiteralRange implements MCCommonLiteralsASTBasicDoubleLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for BasicDoubleLiteral";
  public static final String ERROR_CODE = "0xA0212";

  protected BigDecimal min;
  protected BigDecimal max;

  public BasicDoubleLiteralRange(){
    this.min = BigDecimal.valueOf(-Double.MAX_VALUE);
    this.max = BigDecimal.valueOf(Double.MAX_VALUE);
  }

  public BasicDoubleLiteralRange(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTBasicDoubleLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource());
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
