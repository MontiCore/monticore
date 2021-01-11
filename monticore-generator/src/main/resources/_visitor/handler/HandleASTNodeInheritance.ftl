<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
  getTraverser().visit(node);
<#list classList as clazz>
  if (node instanceof ${clazz}) {
    getTraverser().traverse((${clazz}) node);
  }
</#list>
  getTraverser().endVisit(node);
