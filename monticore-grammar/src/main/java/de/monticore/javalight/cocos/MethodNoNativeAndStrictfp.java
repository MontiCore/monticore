/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.se_rwth.commons.logging.Log;

public class MethodNoNativeAndStrictfp implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0819";

  public static final String ERROR_MESSAGE = "method %s must not be both 'native' and 'strictfp'.";

  @Override
  public void check(ASTMethodDeclaration node) {
    if (node.isPresentSymbol()) {
      JavaMethodSymbol methodSymbol = (JavaMethodSymbol) node.getSymbol();
      if (methodSymbol.isIsNative() && methodSymbol.isIsStrictfp()) {
        Log.error(String.format(ERROR_CODE,ERROR_MESSAGE ,node.getName()),
            node.get_SourcePositionStart());

      }
    }

  }
}
