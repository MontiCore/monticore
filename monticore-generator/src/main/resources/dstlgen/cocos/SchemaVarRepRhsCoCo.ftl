<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "package")}

package ${package}.${grammarNameLower}tr._cocos;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.monticore.tf.tfcommons._ast.ASTAssign;
import de.monticore.tf.tfcommons._ast.ASTTFAssignments;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr._visitor.*;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;

import java.util.List;
import java.util.Set;

public class ${classname} implements ${ast.getName()}TRAST${grammarName}TFRuleCoCo {

  @Override
  public void check(AST${grammarName}TFRule node) {
    if (node.getTFRule().isPresentTFAssignments()) {
      CollectCoCoInformationState state = new CollectCoCoInformationState();
      ${grammarName}TRTraverser traverser = new ${grammarName}CollectRHSVariablesVisitorBuilder().build(state);
      node.accept(traverser);
      ASTTFAssignments assignments = node.getTFRule().getTFAssignments();
      List<String> varsInAssignments = Lists.newArrayList();
      for (ASTAssign a : assignments.getAssignList()) {
        if (state.getVarsOnLHS().contains(a.getVariable())) {
          Log.error(String.format("0xF0C06 Schema variable %s is part of the pattern and must not be assigned.",
              a.getVariable()), a.get_SourcePositionStart());
        }
        if (!varsInAssignments.contains(a.getVariable())) {
          varsInAssignments.add(a.getVariable());
        }
        if (!state.getRHSOnlyVars().contains(a.getVariable())) {
          Log.error(String.format("0xF0C06 Schema variable %s not a schema variable.", a.getVariable()), a.get_SourcePositionStart());
        }
      }

      Set<String> assignableVars = state.getRHSOnlyVars();
      assignableVars.removeAll(state.getVarsOnLHS());
      assignableVars.removeAll(varsInAssignments);
      if(!assignableVars.isEmpty()){
        Log.warn("0xF0C03 Schema variables for names appearing on the right side of a modification should be assigned in the 'Assign' block\"\n"
        + " + \" or given by the user: " + assignableVars.toString());
        }
      }
    }


  public void addTo(${ast.getName()}TRCoCoChecker checker) {
   checker.addCoCo(this);
  }
}
