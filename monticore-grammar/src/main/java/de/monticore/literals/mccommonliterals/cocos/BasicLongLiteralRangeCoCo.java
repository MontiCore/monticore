/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTBasicLongLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigInteger;

public class BasicLongLiteralRangeCoCo implements MCCommonLiteralsASTBasicLongLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for BasicLongLiteral";
  public static final String ERROR_CODE = "0xA0209";

  protected BigInteger min;
  protected BigInteger max;

  public BasicLongLiteralRangeCoCo(){
    this.min = BigInteger.valueOf(Long.MIN_VALUE);
    this.max = BigInteger.valueOf(Long.MAX_VALUE);
  }

  public BasicLongLiteralRangeCoCo(BigInteger min, BigInteger max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTBasicLongLiteral node) {
    BigInteger nodeValue = new BigInteger(node.getSource().substring(0, node.getSource().length()-1));
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
