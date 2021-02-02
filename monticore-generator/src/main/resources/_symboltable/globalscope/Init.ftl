<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeDeSerName", "deSerMap")}
  deSer = new ${scopeDeSerName}();
<#list deSerMap as className, deSerName>
  symbolDeSers.put("${className}", new ${deSerName}());
</#list>