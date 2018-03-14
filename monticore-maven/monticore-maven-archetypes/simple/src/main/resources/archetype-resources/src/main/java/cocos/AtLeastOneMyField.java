/* (c) https://github.com/MontiCore/monticore */

package ${package}.cocos;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._cocos.MyDSLASTMyElementCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneMyField implements MyDSLASTMyElementCoCo {
  
  public static final String ERROR_CODE = "0xC0001";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " The element '%s' should have at least one field.";
  
  @Override
  public void check(ASTMyElement element) {    
    if (element.getMyFields().isEmpty()) {
      // Issue warning...
      Log.warn(String.format(ERROR_MSG_FORMAT, element.getName()),
          element.get_SourcePositionStart());
    }
  }
  
}
