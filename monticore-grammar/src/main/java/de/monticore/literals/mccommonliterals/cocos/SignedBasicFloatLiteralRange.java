/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTSignedBasicFloatLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;

public class SignedBasicFloatLiteralRange implements MCCommonLiteralsASTSignedBasicFloatLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for SignedBasicFloatLiteral";
  public static final String ERROR_CODE = "0xA0215";

  protected BigDecimal min;
  protected BigDecimal max;

  public SignedBasicFloatLiteralRange(){
    this.min = BigDecimal.valueOf(-Float.MAX_VALUE);
    this.max = BigDecimal.valueOf(Float.MAX_VALUE);
  }

  public SignedBasicFloatLiteralRange(BigDecimal min, BigDecimal max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTSignedBasicFloatLiteral node) {
    BigDecimal nodeValue = new BigDecimal(node.getSource().substring(0, node.getSource().length()-1));
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
