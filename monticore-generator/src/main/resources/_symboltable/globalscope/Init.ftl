<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName", "scopeDeSerName", "deSerMap")}
  desers.put("${scopeName}", new ${scopeDeSerName}());
<#list deSerMap as className, deSerName>
  desers.put("${className}", new ${deSerName}());
</#list>