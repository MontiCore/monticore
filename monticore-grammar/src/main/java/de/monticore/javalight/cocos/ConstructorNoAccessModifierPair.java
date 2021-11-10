/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.List;

public class ConstructorNoAccessModifierPair implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0302";

  public static final String ERROR_MSG_FORMAT = " Formal parameter '%s' is already declared in constructor '%s'. ";

  @Override
  public void check(ASTConstructorDeclaration node) {

  }
/*
  // JLS3 8.8.3-2
  @Override
  public void check(ASTConstructorDeclaration node) {
    JavaMethodSymbol symbol = node.getSymbol();
    List<String> listModifier = new ArrayList<>();
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      modifier.accept(typeResolver);
      JavaTypeSymbolReference mod = typeResolver.getResult().get();
      listModifier.add(mod.getName());
    }
    if (listModifier.contains("public") && listModifier.contains("protected") && listModifier
        .contains("private")) {
      Log.error(
          "0xA0307 modifiers 'public', 'protected' and private are mentioned in the same constructor declaration '"
              + node.getName() + "'.",
          node.get_SourcePositionStart());
      return;
    }
    if (listModifier.contains("public") && listModifier.contains("protected")) {
      Log.error(
          "0xA0308 modifiers 'public' and 'protected' are mentioned in the same constructor declaration '"
              + node.getName() + "'.",
          node.get_SourcePositionStart());
      return;
    }
    if (listModifier.contains("public") && listModifier.contains("private")) {
      Log.error(
          "0xA0309 modifiers 'public' and 'private' are mentioned in the same constructor declaration '"
              + node.getName() + "'.",
          node.get_SourcePositionStart());
      return;
    }
    if (listModifier.contains("protected") && listModifier.contains("private")) {
      Log.error(
          "0xA0310 modifiers 'protected' and 'private' are mentioned in the same constructor declaration '"
              + node.getName() + "'.",
          node.get_SourcePositionStart());
      return;
    }
  }
*/
}
