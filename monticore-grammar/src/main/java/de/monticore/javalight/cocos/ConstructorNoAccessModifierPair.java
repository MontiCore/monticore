/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightFullPrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ConstructorNoAccessModifierPair implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0809";

  public static final String ERROR_MSG_FORMAT = " Invalid modifiers are mentioned in constructor's '%s' declaration.";

  // JLS3 8.8.3-2
  @Override
  public void check(ASTConstructorDeclaration node) {
    JavaMethodSymbol symbol = node.getSymbol();
    if((symbol.isIsPublic() && symbol.isIsProtected() && symbol.isIsPrivate())
      || (symbol.isIsPublic() && symbol.isIsProtected())
      || (symbol.isIsPublic() && symbol.isIsPrivate())
      || (symbol.isIsProtected() && symbol.isIsPrivate())) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(),
          node.get_SourcePositionStart()));
    }
  }
}


