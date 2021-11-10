/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;


import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.se_rwth.commons.logging.Log;

public class MethodAbstractAndOtherModifiers implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0802";

  public static final String ERROR_MSG_FORMAT = " The abstract method %s must be public. ";

  //JLS3 8.4.3-3
  @Override
  public void check(ASTMethodDeclaration node) {
    JavaMethodSymbol methodSymbol = node.getSymbol();
    if (methodSymbol.isIsAbstract()) {
      if (methodSymbol.isIsPrivate() || methodSymbol.isIsStatic() || methodSymbol.isIsFinal() ||
          methodSymbol.isIsNative() || methodSymbol.isIsStrictfp() || methodSymbol.isIsSynchronized()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }

}

