/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class ConstructorNoAccessModifierPair implements JavaLightASTConstructorDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0809";
  
  public static final String ERROR_MSG_FORMAT =
      " Conflicting access modifiers are mentioned in constructor's '%s' declaration at %s.";
  
  protected boolean isAccessModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.PUBLIC
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PROTECTED
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PRIVATE;
  }
  
  // JLS17 8.8.3-2
  @Override
  public void check(ASTConstructorDeclaration node) {
    boolean hasAccessModifier = false;
    List<ASTMCModifier> modifiers = node.getMCModifierList();
    
    for (ASTMCModifier modifier : modifiers) {
      if (JavaLightMill.typeDispatcher().isMCCommonStatementsASTJavaModifier(modifier)) {
        ASTJavaModifier javaModifier =
            JavaLightMill.typeDispatcher().asMCCommonStatementsASTJavaModifier(modifier);
        if (isAccessModifier(javaModifier)) {
          if (hasAccessModifier) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(),
                node.get_SourcePositionStart()));
            break;
          }
          else {
            hasAccessModifier = true;
          }
        }
      }
    }
  }
}


