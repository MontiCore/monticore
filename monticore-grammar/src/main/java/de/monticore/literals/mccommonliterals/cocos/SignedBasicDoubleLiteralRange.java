/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTSignedBasicDoubleLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class SignedBasicDoubleLiteralRange implements MCCommonLiteralsASTSignedBasicDoubleLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for SignedBasicDoubleLiteral";
  public static final String ERROR_CODE = "0xA0214";

  protected BigDecimal min;
  protected BigDecimal max;

  public SignedBasicDoubleLiteralRange(){
    this.min = BigDecimal.valueOf(-Double.MAX_VALUE);
    this.max = BigDecimal.valueOf(Double.MAX_VALUE);
  }

  public SignedBasicDoubleLiteralRange(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTSignedBasicDoubleLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource());
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
