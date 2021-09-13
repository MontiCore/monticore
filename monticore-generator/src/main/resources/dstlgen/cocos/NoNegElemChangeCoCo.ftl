<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;

<#assign service = glex.getGlobalVar("service")>
/**
 * This CoCo makes sure that no negative element is changed.
 */
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_NegCoCo {

  @Override
  public void check(${package}.${grammarNameLower}tr._ast.AST${prod.getName()}_Neg node) {
    ${grammarName}RepElemVisitor v = new ${grammarName}RepElemVisitor();
    node.accept(v.getTraverser());

    if(v.getRepElements() > 0) {
        Log.error(String.format("0xF0C09${service.getGeneratedErrorCode(classname + prod.getName())} Negative elements must not be changed.",
            node.getClass().getName()),
            node.get_SourcePositionStart());
    }
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }

}
