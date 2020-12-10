<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName", "scopeDeSerName", "deSerNames")}
  desers.put("${scopeName}", new ${scopeDeSerName}());
<#list deSerNames as deSerName>
  desers.put("${deSerName}", new ${deSerName}DeSer());
</#list>