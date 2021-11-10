/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

public class MethodReturnVoid implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0820";

  public static final String ERROR_MESSAGE = "Invalid return type for method %s. The void type must have dimension 0.";

  @Override
  public void check(ASTMethodDeclaration node) {
    if (node.getMCReturnType().isPresentMCVoidType()
        && node.getDimList().size() > 0) {
      Log.error(String.format(ERROR_CODE,ERROR_MESSAGE ,node.getName()),
          node.get_SourcePositionStart());
    }
  }
}

