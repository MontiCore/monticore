<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbols", "scopeType")}
  // traverse symbols within the scope
<#list symbols as symbol>
    <#assign simpleName = symbol>
    <#if symbol?contains(".")>
      <#assign simpleName= symbol?substring(symbol?last_index_of(".")+1)>
    </#if>
  for (${symbol} s : node.getLocal${simpleName}s()) {
    s.accept(getTraverser());
  }
</#list>

  // traverse sub-scopes
  for (${scopeType} scope : node.getSubScopes()) {
    scope.accept(getTraverser());
  }