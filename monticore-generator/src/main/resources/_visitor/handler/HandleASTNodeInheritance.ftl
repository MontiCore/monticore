<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
  getTraverser().visit(node);
<#assign if="if">
<#list classList as clazz>
  ${if} (node instanceof ${clazz}) {
    getTraverser().traverse((${clazz}) node);
  }
<#assign if="else if">
</#list>
  getTraverser().endVisit(node);
