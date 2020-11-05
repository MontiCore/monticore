<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("resolverMethodList")}
  clearLoadedFiles();
<#list resolverMethodList as resolverMethod>
  ${resolverMethod}().clear();
</#list>