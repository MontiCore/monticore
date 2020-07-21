<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolList", "superScopes")}
<#list symbolList as symbol>
  if (!node.getLocal${symbol}Symbols().isEmpty()) {
    node.getLocal${symbol}Symbols().stream().forEach(s -> s.accept(getRealThis()));
  }
</#list>
<#list superScopes as scope>
  getRealThis().traverse((${scope}) node);
</#list>