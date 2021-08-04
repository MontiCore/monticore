<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;

/**
 * This CoCo makes sure that no negative element is created or added.
 */
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_RepCoCo {

  @Override
  public void check(${package}.${grammarNameLower}tr._ast.AST${prod.getName()}_Rep node) {
    ${grammarName}RepElemVisitor v = new ${grammarName}RepElemVisitor();

    if(node.isPresentRhs()) {
        node.getRhs().accept(v.getTraverser());

        if (v.getRepElements() > 0) {
            Log.error(String.format("0xF0C07 Replacements on the RHS are forbidden.",
                node.getClass().getName()),
                node.get_SourcePositionStart());
        }
    }
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }

}
