/* (c) https://github.com/MontiCore/monticore */

package ${package}.cocos;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;

import ${package}.mydsl._ast.ASTMyField;
import ${package}.mydsl._cocos.MyDSLASTMyFieldCoCo;
import ${package}.symboltable.MyElementSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class ExistingMyFieldType implements MyDSLASTMyFieldCoCo {
  
  public static final String ERROR_CODE = "0xC0002";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " The referenced element '%s' of the field '%s' does not exist.";
  
  @Override
  public void check(ASTMyField field) {
    checkArgument(field.getEnclosingScope().isPresent());
    
    Scope enclosingScope = field.getEnclosingScope().get();
    Optional<MyElementSymbol> typeElement = enclosingScope.resolve(field.getType(), MyElementSymbol.KIND);
    
    if (!typeElement.isPresent()) {
      // Issue error...
      Log.error(String.format(ERROR_MSG_FORMAT, field.getType(), field.getName()),
          field.get_SourcePositionStart());
    }
  }
}
