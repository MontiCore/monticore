<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "cds", "visitorService")}
  ${type} traverser = new ${type}();
<#list cds as cd>
  traverser.set${cd.getName()}Handler(new ${visitorService.getInheritanceHandlerFullName(cd)}());
</#list>
  return traverser;