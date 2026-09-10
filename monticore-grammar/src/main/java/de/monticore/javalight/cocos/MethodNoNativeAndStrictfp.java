/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

public class MethodNoNativeAndStrictfp implements JavaLightASTMethodDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0819";
  
  public static final String ERROR_MESSAGE = "Method %s must not be both 'native' and 'strictfp'.";
  
  protected boolean isNativeModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.NATIVE;
  }
  
  protected boolean isStrictFpModifier(ASTJavaModifier modifier) {
    return modifier.getModifier() == ASTConstantsMCCommonStatements.STRICTFP;
  }
  
  @Override
  public void check(ASTMethodDeclaration node) {
    boolean isNative = false;
    boolean isStrictFp = false;
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      if (JavaLightMill.typeDispatcher().isMCCommonStatementsASTJavaModifier(modifier)) {
        ASTJavaModifier javaModifier =
            JavaLightMill.typeDispatcher().asMCCommonStatementsASTJavaModifier(modifier);
        if (isNativeModifier(javaModifier)) {
          isNative = true;
        }
        else if (isStrictFpModifier(javaModifier)) {
          isStrictFp = true;
        }
        if (isNative && isStrictFp) {
          Log.error(String.format(ERROR_CODE + ERROR_MESSAGE, node.getName()),
              node.get_SourcePositionStart());
          break;
        }
      }
    }
  }
}
