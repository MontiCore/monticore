/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals.cocos;

import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsASTIntLiteralCoCo;
import de.se_rwth.commons.logging.Log;

import java.math.BigInteger;

public class IntLiteralRange implements MCJavaLiteralsASTIntLiteralCoCo {

  public static final String ERROR_MSG = " number %s not in range [%s,%s] for IntLiteral";
  public static final String ERROR_CODE = "0xA0216";

  protected BigInteger min;
  protected BigInteger max;

  public IntLiteralRange(){
    this.min = BigInteger.valueOf(Integer.MIN_VALUE);
    this.max = BigInteger.valueOf(Integer.MAX_VALUE);
  }

  public IntLiteralRange(BigInteger min, BigInteger max){
    this.min = min;
    this.max = max;
  }


  @Override
  public void check(ASTIntLiteral node) {
    BigInteger nodeValue;
    if(node.getSource().startsWith("-0x") || node.getSource().startsWith("-0X")){
      nodeValue = new BigInteger("-" + node.getSource().substring(3), 16);
    }else if(node.getSource().startsWith("0X") || node.getSource().startsWith("0x")){
      nodeValue = new BigInteger(node.getSource().substring(2), 16);
    }else if(node.getSource().startsWith("-0") || node.getSource().startsWith("0")){
      nodeValue = new BigInteger(node.getSource(), 8);
    }else if(node.getSource().startsWith("-0b") || node.getSource().startsWith("-0B")){
      nodeValue = new BigInteger("-" + node.getSource().substring(3), 2);
    }else if(node.getSource().startsWith("0b") || node.getSource().startsWith("0B")){
      nodeValue = new BigInteger(node.getSource().substring(2), 2);
    }else{
      nodeValue = new BigInteger(node.getSource());
    }
    if(nodeValue.compareTo(this.min) < 0 || nodeValue.compareTo(this.max) > 0) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, nodeValue, min, max));
    }
  }


}
