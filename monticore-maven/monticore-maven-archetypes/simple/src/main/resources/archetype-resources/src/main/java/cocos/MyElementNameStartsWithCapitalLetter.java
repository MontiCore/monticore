/* (c) https://github.com/MontiCore/monticore */

package ${package}.cocos;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._cocos.MyDSLASTMyElementCoCo;
import de.se_rwth.commons.logging.Log;

public class MyElementNameStartsWithCapitalLetter implements MyDSLASTMyElementCoCo {
  
  public static final String ERROR_CODE = "0xC0003";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " Element name '%s' should start with a capital letter.";
  
  @Override
  public void check(ASTMyElement element) {
    String elementName = element.getName();
    boolean startsWithUpperCase = Character.isUpperCase(elementName.charAt(0));
    
    if (!startsWithUpperCase) {
      // Issue warning...
      Log.warn(String.format(ERROR_MSG_FORMAT, elementName), element.get_SourcePositionStart());
    }
  }
  
}
