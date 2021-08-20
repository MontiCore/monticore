<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import ${package}.${grammarNameLower}tr._ast.*;

/**
 * This CoCo makes sure no optional nodes appear within negative nodes.
 */
public class ${className}
<#list productions>
    implements
    <#items as prod>
        ${ast.getName()}TRAST${prod.getName()}_OptCoCo
        <#sep>,
    </#items>
</#list>
{
  <#list productions as prod>
  private NoOptWithinNotCoCo_${prod.getName()} coco${prod.getName()};
  </#list>

  public ${className}() {
    <#list productions as prod>
        {
        ${package}.${grammarNameLower}tr._visitor.${ast.getName()}TRTraverser t
            = ${package}.${grammarNameLower}tr.${ast.getName()}TRMill.inheritanceTraverser();
      t.add4${ast.getName()}TR(this);
      coco${prod.getName()} = new NoOptWithinNotCoCo_${prod.getName()}(t);
        }
    </#list>
  }


  <#list productions as prod>
  @Override
  public void check(AST${prod.getName()}_Opt node) {
    Log.error("Negative nodes must not contain optional nodes.", node.get_SourcePositionStart());
  }
  </#list>

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    <#list productions as prod>
    checker.addCoCo(coco${prod.getName()});
    </#list>
  }
}
