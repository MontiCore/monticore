/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

public class MethodModifiersValid implements JavaLightASTMethodDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0822";
  
  public static final String ERROR_MSG_FORMAT =
      "  Method '%s' has modifier %s that must not be used.";
  
  protected boolean isValidModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.PUBLIC
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PROTECTED
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PRIVATE
        || modifier.getModifier() == ASTConstantsMCCommonStatements.ABSTRACT
        || modifier.getModifier() == ASTConstantsMCCommonStatements.STATIC
        || modifier.getModifier() == ASTConstantsMCCommonStatements.FINAL
        || modifier.getModifier() == ASTConstantsMCCommonStatements.SYNCHRONIZED
        || modifier.getModifier() == ASTConstantsMCCommonStatements.NATIVE
        || modifier.getModifier() == ASTConstantsMCCommonStatements.STRICTFP
        || modifier.getModifier() == ASTConstantsMCCommonStatements.MODIFIER_DEFAULT;
  }
  
  // JLS17 8.4.3 (MethodModifier) / 9.4 (InterfaceMethodModifier)
  @Override
  public void check(ASTMethodDeclaration node) {
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      if (JavaLightMill.typeDispatcher().isMCCommonStatementsASTJavaModifier(modifier)) {
        ASTJavaModifier javaModifier =
            JavaLightMill.typeDispatcher().asMCCommonStatementsASTJavaModifier(modifier);
        if (!isValidModifier(javaModifier)) {
          String modifierName = JavaLightMill.prettyPrint(modifier, false);
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(), modifierName),
              node.get_SourcePositionStart());
        }
      }
    }
  }
}
