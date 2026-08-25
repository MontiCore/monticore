/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class ConstructorNoDuplicateModifier implements JavaLightASTConstructorDeclarationCoCo {
  
  public static final String ERROR_CODE = "0xA0808";
  
  public static final String ERROR_MSG_FORMAT =
      " Modifier '%s' is mentioned more than once in the constructor '%s'. ";
  
  // JLS17 8.8.3-1
  @Override
  public void check(ASTConstructorDeclaration node) {
    List<ASTMCModifier> modifiers = node.getMCModifierList();
    for (int i = 0; i < modifiers.size(); i++) {
      ASTMCModifier modifier = modifiers.get(i);
      for (int j = i + 1; j < modifiers.size(); j++) {
        ASTMCModifier modifier2 = modifiers.get(j);
        if (modifier2.deepEquals(modifier)) {
          String modifierName = JavaLightMill.prettyPrint(modifier2, false);
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, modifierName, node.getName()),
              node.get_SourcePositionStart());
          break;
        }
      }
    }
  }
}
