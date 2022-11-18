/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals.cocos;

import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsASTLongLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigInteger;

public class LongLiteralRange implements MCJavaLiteralsASTLongLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for LongLiteral";
  public static final String ERROR_CODE = "0xA0217";

  protected BigInteger min;
  protected BigInteger max;

  public LongLiteralRange(){
    this.min = BigInteger.valueOf(Long.MIN_VALUE);
    this.max = BigInteger.valueOf(Long.MAX_VALUE);
  }

  public LongLiteralRange(BigInteger min, BigInteger max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTLongLiteral node) {
    BigInteger nodeValue;
    String source = node.getSource().substring(0, node.getSource().length()-1);
    if(source.startsWith("-0x") || source.startsWith("-0X")){
      nodeValue = new BigInteger("-" + source.substring(3), 16);
    }else if(source.startsWith("0X") || source.startsWith("0x")){
      nodeValue = new BigInteger(source.substring(2), 16);
    }else if(source.startsWith("-0") || source.startsWith("0")){
      nodeValue = new BigInteger(source, 8);
    }else if(source.startsWith("-0b") || source.startsWith("-0B")){
      nodeValue = new BigInteger("-" + source.substring(3), 2);
    }else if(source.startsWith("0b") || source.startsWith("0B")){
      nodeValue = new BigInteger(source.substring(2), 2);
    }else{
      nodeValue = new BigInteger(source);
    }
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }

}
