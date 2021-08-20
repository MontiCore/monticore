<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}

package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import ${package}.${grammarNameLower}tr._visitor.${grammarName}TRTraverser;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;

/**
* This CoCo makes sure that no negative elements are nested.
*/
public class ${className} implements ${ast.getName()}TRAST${grammarName}TFRuleCoCo {

  @Override
  public void check(${package}.${grammarNameLower}tr._ast.AST${grammarName}TFRule node) {
    CollectCoCoInformationState state = new CollectCoCoInformationState();
    ${grammarName}TRTraverser traverser = new ${grammarName}CollectRHSVariablesVisitorBuilder().build(state);
    node.accept(traverser);

    if (state.getVarsOnRHS().contains("$_")) {
      Log.error(String.format("0xF0C15 Schema variables on the RHS of replacements must not be anonymous."));
    }
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }

}
