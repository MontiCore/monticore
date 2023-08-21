/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstructorNoAccessModifierPair implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0809";

  public static final String ERROR_MSG_FORMAT = " Invalid modifiers are mentioned in constructor's '%s' declaration at %s.";

  // JLS3 8.8.3-2
  @Override
  public void check(ASTConstructorDeclaration node) {
    List<ASTMCModifier> modifiers = node.getMCModifierList();

    int mod = 0;

    for (ASTMCModifier modifier : modifiers) {
      ASTJavaModifier javaModifier = JavaLightMill.typeDispatcher().asASTJavaModifier(modifier);

      if (javaModifier.getModifier() >= ASTConstantsMCCommonStatements.PRIVATE
          && javaModifier.getModifier() <=  ASTConstantsMCCommonStatements.PUBLIC) {
        if(mod != 0 && mod != javaModifier.getModifier()) {
         Log.error(String.format(ERROR_CODE+ERROR_MSG_FORMAT, node.getName(),node.get_SourcePositionStart()));
        }
        mod = javaModifier.getModifier();
      }
    }
  }
}


