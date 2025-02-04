/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.se_rwth.commons.logging.Log;

public class ConstructorModifiersValid implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0820";

  public static final String ERROR_MSG_FORMAT = "  Constructor '%s' cannot be declared 'abstract', 'final', 'static' or 'native'.";

  @Override
  public void check(ASTConstructorDeclaration node) {
    JavaMethodSymbol symbol = node.getSymbol();
    if (symbol.isIsAbstract() || symbol.isIsFinal() || symbol.isIsStatic() || symbol.isIsNative()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT ,node.getName()),
          node.get_SourcePositionStart());
    }
  }
}
