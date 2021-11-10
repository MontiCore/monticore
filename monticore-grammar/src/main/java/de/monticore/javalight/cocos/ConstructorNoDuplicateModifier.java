/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;

public class ConstructorNoDuplicateModifier implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0302";

  public static final String ERROR_MSG_FORMAT = " Formal parameter '%s' is already declared in constructor '%s'. ";

  @Override
  public void check(ASTConstructorDeclaration node) {

  }

/*  // JLS3 8.8.3-1
  @Override
  public void check(ASTConstructorDeclaration node) {
    List<String> modifiers = new ArrayList<>();
 //print the modifier -> add to list
    for (ASTModifier modifier : node.getMCModifierList()) {
      if (modifier instanceof ASTPrimitiveModifier) {
        modifier.accept(typeResolver);
        JavaTypeSymbolReference modType = typeResolver.getResult()
            .get();
        if (modifiers.contains(modType.getName())) {
          Log.error("0xA0311 modifier '" + modType.getName()
              + "' is mentioned more than once in the constructor declaration '" + node.getName()
              + "'.", node.get_SourcePositionStart());
        }
        else {
          modifiers.add(modType.getName());
        }
      }
    }
  }
*/

}
