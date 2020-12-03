<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("deSerNames")}
<#list deSerNames as deSerName>
  desers.put("${deSerName}", new ${deSerName}DeSer());
</#list>