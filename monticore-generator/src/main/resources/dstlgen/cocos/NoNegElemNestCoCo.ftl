<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;

<#assign service = glex.getGlobalVar("service")>
/**
* This CoCo makes sure that no negative elements are nested.
*/
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_NegCoCo {

  @Override
  public void check(${package}.${grammarNameLower}tr._ast.AST${prod.getName()}_Neg node) {
      ${grammarName}NegElemVisitor v = new ${grammarName}NegElemVisitor();

      node.accept(v.getTraverser());

      if (v.getNegElements() > 1) {
        Log.error(String.format("0xF0C08${service.getGeneratedErrorCode(classname + prod.getName())} Negative Elements must not be nested.\n To further restrict negative elements use 'Where'.",
            node.getClass().getName()),
            node.get_SourcePositionStart());
      }

  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }

}
