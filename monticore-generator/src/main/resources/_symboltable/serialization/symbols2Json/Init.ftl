<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("globalScope", "scopeDeSerName", "scopeName", "mill", "deSerMap", "printerMap")}
<#assign service = glex.getGlobalVar("service")>
  ${globalScope} gs = ${mill}.globalScope();
  scopeDeSer = (${scopeDeSerName}) gs.getDeSer("${scopeName}");
  <#list deSerMap as name, deSer>
  ${name?uncap_first} = (${deSer}DeSer) gs.getDeSer("${deSer}");
  </#list>
  <#list printerMap as name, printer>
  traverser.add4${name}(new ${printer}(getTraverser(), getJsonPrinter()));
  </#list>
