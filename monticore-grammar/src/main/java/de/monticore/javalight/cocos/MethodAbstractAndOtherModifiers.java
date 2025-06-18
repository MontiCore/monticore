/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

public class MethodAbstractAndOtherModifiers implements JavaLightASTMethodDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0802";
  
  public static final String ERROR_MSG_FORMAT = " The abstract method %s must be public. ";
  
  protected boolean isValidModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.PUBLIC
        || modifier.getModifier() == ASTConstantsMCCommonStatements.PROTECTED;
  }
  
  protected boolean isAbstractModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.ABSTRACT;
  }
  
  //JLS3 8.4.3-3
  @Override
  public void check(ASTMethodDeclaration node) {
    boolean isAbstractMethod = false;
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      if (JavaLightMill.typeDispatcher().isMCCommonStatementsASTJavaModifier(modifier)) {
        ASTJavaModifier javaModifier =
            JavaLightMill.typeDispatcher().asMCCommonStatementsASTJavaModifier(modifier);
        if (!isAbstractMethod && isAbstractModifier(javaModifier)) {
          isAbstractMethod = true;
        }
        else {
          if (!isValidModifier(javaModifier)) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
                node.get_SourcePositionStart());
            break;
          }
        }
      }
    }
  }
}

