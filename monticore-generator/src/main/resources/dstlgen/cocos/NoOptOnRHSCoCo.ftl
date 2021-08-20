<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr._visitor.${ast.getName()}TRVisitor2;

/**
 * This CoCo makes sure no optional nodes appear on the right-hand side of replacements.
 */
public class ${className}
<#list productions>
    implements //${ast.getName()}TRVisitor2
    <#items as prod>
        ${ast.getName()}TRAST${prod.getName()}_OptCoCo
        <#sep>,
    </#items>
</#list>

{
  <#list productions as prod>
  private NoOptOnRHSCoCo_${prod.getName()} coco${prod.getName()};
  </#list>

  public ${className}() {
    <#list productions as prod>
        {
        ${package}.${grammarNameLower}tr._visitor.${ast.getName()}TRTraverser t
        = ${package}.${grammarNameLower}tr.${ast.getName()}TRMill.inheritanceTraverser();
        t.add4${ast.getName()}TR(this);
        coco${prod.getName()} = new NoOptOnRHSCoCo_${prod.getName()}(t);
        }
    </#list>
  }

  <#list productions as prod>
  @Override
  public void check(AST${prod.getName()}_Opt node) {
    Log.error("Optional nodes must not appear on the right-hand side of replacements.", node.get_SourcePositionStart());
  }
  </#list>

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    <#list productions as prod>
    checker.addCoCo(coco${prod.getName()});
    </#list>
  }
}
