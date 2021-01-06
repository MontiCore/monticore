<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName", "cds", "visitorService")}
  traverser = ${millName}.traverser();
<#list cds as cd>
  traverser.set${cd.getName()}Handler(new ${visitorService.getInheritanceHandlerFullName(cd)}());
</#list>