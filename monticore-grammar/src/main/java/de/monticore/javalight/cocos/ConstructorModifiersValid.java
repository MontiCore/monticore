/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

public class ConstructorModifiersValid implements JavaLightASTConstructorDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0820";
  
  public static final String ERROR_MSG_FORMAT =
      "  Constructor '%s' modifier can only be one of 'public', 'protected', 'private'.";
  
  protected boolean isValidModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.PUBLIC
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PROTECTED
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PRIVATE;
  }
  
  // JLS17 8.8.3-3
  @Override
  public void check(ASTConstructorDeclaration node) {
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      if (JavaLightMill.typeDispatcher().isMCCommonStatementsASTJavaModifier(modifier)) {
        ASTJavaModifier javaModifier =
            JavaLightMill.typeDispatcher().asMCCommonStatementsASTJavaModifier(modifier);
        if (!isValidModifier(javaModifier)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
              node.get_SourcePositionStart());
        }
      }
    }
  }
}
