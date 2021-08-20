<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;

/**
 * This CoCo makes sure that no replacement is empty.
 */
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_RepCoCo {

  @Override
  public void check(${package}.${grammarNameLower}tr._ast.AST${prod.getName()}_Rep node) {
    if(!node.isPresentLhs() && !node.isPresentRhs()) {
      Log.warn(String.format("0xF0C12 Replacements should not be empty"), node.get_SourcePositionStart());
    }
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }
}
