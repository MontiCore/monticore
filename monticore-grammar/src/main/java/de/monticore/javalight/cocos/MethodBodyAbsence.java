/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.se_rwth.commons.logging.Log;

public class MethodBodyAbsence implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0804";

  public static final String ERROR_MESSAGE = "Method '%s' must be 'abstract' or 'native' if it doesn't specify a body.";

  @Override
  public void check(ASTMethodDeclaration node) {
    if (node.isPresentSymbol()) {
      JavaMethodSymbol methodSymbol = node.getSymbol();
      // JLS3 8.4.7-1
      if ((!node.isPresentMCJavaBlock() && !methodSymbol.isIsAbstract()) && (!node.isPresentMCJavaBlock() && !methodSymbol.isIsNative())) {
        Log.error(String.format(ERROR_CODE + ERROR_MESSAGE, methodSymbol.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
