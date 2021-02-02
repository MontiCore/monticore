<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeDeSerName", "deSerMap")}
  deser = new ${scopeDeSerName}();
<#list deSerMap as className, deSerName>
  desers.put("${className}", new ${deSerName}());
</#list>