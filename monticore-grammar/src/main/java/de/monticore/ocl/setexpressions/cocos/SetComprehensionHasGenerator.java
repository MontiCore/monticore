// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.setexpressions.cocos;

import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.ocl.setexpressions._cocos.SetExpressionsASTSetComprehensionCoCo;
import de.se_rwth.commons.logging.Log;

public class SetComprehensionHasGenerator implements SetExpressionsASTSetComprehensionCoCo {

  @Override
  public void check(ASTSetComprehension node) {
    if (!node.getLeft().isPresentGeneratorDeclaration()
        && !node.getLeft().isPresentSetVariableDeclaration()) {
      for (ASTSetComprehensionItem setComprehensionItem : node.getSetComprehensionItemList()) {
        if (setComprehensionItem.isPresentGeneratorDeclaration()) {
          return;
        }
      }
      Log.error(
          "0xOCL24 SetComprehension requires at least one generator or a variable Declaration");
    }
  }
}
