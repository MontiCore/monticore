/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTSignedNatLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigInteger;

public class SignedNatLiteralRange implements MCCommonLiteralsASTSignedNatLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for SignedNatLiteral";
  public static final String ERROR_CODE = "0xA0210";

  protected BigInteger min;
  protected BigInteger max;

  public SignedNatLiteralRange(){
    this.min = BigInteger.valueOf(Integer.MIN_VALUE);
    this.max = BigInteger.valueOf(Integer.MAX_VALUE);
  }

  public SignedNatLiteralRange(BigInteger min, BigInteger max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTSignedNatLiteral node) {
    BigInteger nodeValue = new BigInteger(node.getSource());
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
