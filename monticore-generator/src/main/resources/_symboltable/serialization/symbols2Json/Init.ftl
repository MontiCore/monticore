<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("globalScope", "mill", "deSerMap", "grammar")}
<#assign service = glex.getGlobalVar("service")>
  ${globalScope} gs = ${mill}.globalScope();
  <#list deSerMap as name, deSer>
  ${name?uncap_first} = (${deSer}DeSer) gs.getDeSer("${deSer}");
  </#list>
  traverser.add4${grammar}(this);
