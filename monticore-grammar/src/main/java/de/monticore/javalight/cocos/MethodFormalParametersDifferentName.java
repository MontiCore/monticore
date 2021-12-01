/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MethodFormalParametersDifferentName implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0812";

  public static final String ERROR_MSG_FORMAT = " Formal parameter '%s' is already declared in method '%s'.";

  //JLS3 8.4.1-1
  @Override
  public void check(ASTMethodDeclaration node) {
    Collection<String> names = new HashSet<>();
    if (node.getFormalParameters().isPresentFormalParameterListing()) {
      if(node.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()){
        names.add(node.getFormalParameters().getFormalParameterListing().getLastFormalParameter()
            .getDeclaratorId().getName());
      }
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
