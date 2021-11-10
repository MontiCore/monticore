/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTLastFormalParameter;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTLastFormalParameterCoCo;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.se_rwth.commons.logging.Log;

public class MethodFormalParametersVarargsNoArray implements JavaLightASTLastFormalParameterCoCo {

  public static final String ERROR_CODE = "0xA0813";

  public static final String ERROR_MESSAGE = "Variable argument '%s' must not be an array in method.";

  @Override
  public void check(ASTLastFormalParameter node) {
    if((node.getMCType() instanceof ASTMCArrayType)){
      Log.error(String.format(ERROR_CODE + ERROR_MESSAGE, node.getDeclaratorId().getName()), node.get_SourcePositionStart());
    }
  }

}



