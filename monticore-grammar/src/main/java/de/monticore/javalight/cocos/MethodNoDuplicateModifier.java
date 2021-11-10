/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.symboltable.modifiers.Modifier;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class MethodNoDuplicateModifier implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0818";

  public static final String ERROR_MESSAGE = "modifier %s is declared more than once in method %s";

  @Override
  public void check(ASTMethodDeclaration node) {

  }

 /* //JLS3 8.4.3-1
  @Override
  public void check(ASTMethodDeclaration node) {
    List<String> modifiers = new ArrayList<>();
    for (ASTModifier modifier : node.getModifierList()) {
      if (modifier instanceof Modifier) {
        modifier.accept(typeResolver);

        JavaTypeSymbolReference modType = typeResolver.getResult()
            .get();
        if (modifiers.contains(modType.getName())) {
          Log.error(String.format(ERROR_CODE,ERROR_MESSAGE ,modType.getName()),
              node.get_SourcePositionStart());

        } else {
          modifiers.add(modType.getName());
        }
      }
    }*/

  }
