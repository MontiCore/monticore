/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightFullPrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ConstructorNoAccessModifierPair implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0302";

  public static final String ERROR_MSG_FORMAT = " Formal parameter '%s' is already declared in constructor '%s'. ";

  protected String prettyprint(ASTMCModifier a) {
    JavaLightFullPrettyPrinter printer = new JavaLightFullPrettyPrinter(new IndentPrinter());
    a.accept(printer.getTraverser());
    return printer.getPrinter().getContent();
  }
  // JLS3 8.8.3-2
  @Override
  public void check(ASTConstructorDeclaration node) {
    JavaMethodSymbol symbol = node.getSymbol();
    List<String> listModifier = new ArrayList<>();
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      listModifier.add(prettyprint(modifier));
    }
    if ((listModifier.contains("public") && listModifier.contains("protected") && listModifier
        .contains("private")) || (listModifier.contains("public") && listModifier.contains("protected"))
        || (listModifier.contains("public") && listModifier.contains("private"))
        || (listModifier.contains("protected") && listModifier.contains("private"))) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(),
          node.get_SourcePositionStart()));
    }
  }
}


