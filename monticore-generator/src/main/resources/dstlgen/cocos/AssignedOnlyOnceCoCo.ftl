<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "package")}

package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import de.monticore.tf.tfcommons._ast.ASTAssign;
import de.monticore.tf.tfcommons._ast.ASTTFAssignments;
import de.monticore.tf.tfcommons._cocos.TFCommonsASTTFAssignmentsCoCo;

import java.util.ArrayList;
import java.util.List;

/**
* This CoCo makes warns of assigning to the same variable multiple times
*/
public class ${classname} implements TFCommonsASTTFAssignmentsCoCo {

  @Override
  public void check(ASTTFAssignments assignments) {

    List<String> assignedVariables = new ArrayList<String>();
    for (ASTAssign singleAssign : assignments.getAssignList()) {
      if (assignedVariables.contains(singleAssign.getVariable())) {
        Log.warn(String.format("0xF0C17 The variable %s is assigned several times. Assigned variables should be unique.",
          singleAssign.getVariable()), singleAssign.get_SourcePositionStart());
      }
      assignedVariables.add(singleAssign.getVariable());
    }
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }
}
