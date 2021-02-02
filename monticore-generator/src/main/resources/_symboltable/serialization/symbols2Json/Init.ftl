<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("globalScope", "scopeDeSerName", "scopeName", "mill", "deSerMap")}
<#assign service = glex.getGlobalVar("service")>
  ${globalScope} gs = ${mill}.globalScope();
  scopeDeSer = (${scopeDeSerName}) gs.getDeSer();
  <#list deSerMap as name, deSer>
  ${name?uncap_first} = (${deSer}DeSer) gs.getSymbolDeSer("${deSer}");
  </#list>
