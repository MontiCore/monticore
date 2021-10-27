/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class MethodFormalParametersDifferentName implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0812";

  public static final String ERROR_MSG_FORMAT = " Formal parameter '%s' is already declared in method '%s'.";

  //JLS3 8.4.1-1
  @Override
  public void check(ASTMethodDeclaration node) {
    List<String> names = new ArrayList<>();
    if (node.getFormalParameters().isPresentFormalParameterListing()) {
      for (ASTFormalParameter formalParameter : node.getFormalParameters()
          .getFormalParameterListing().getFormalParameterList()) {
        if (names.contains(formalParameter.getDeclaratorId().getName())) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, formalParameter.getDeclaratorId().getName(), node.getName()),
                  node.get_SourcePositionStart());
        }
        else {
          names.add(formalParameter.getDeclaratorId().getName());
        }
      }
    }
  }
}
