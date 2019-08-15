<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superSymbols", "ownSymbol")}
  // traverse symbols within the scope
<#list superSymbols as superSymbol>
    <#assign simpleName = superSymbol>
    <#if superSymbol?contains(".")>
      <#assign simpleName= superSymbol?substring(superSymbol?last_index_of(".")+1)>
    </#if>
  for (${superSymbol} s : scope.getLocal${simpleName}s()) {
    s.accept(getRealThis());
  }
</#list>

  // traverse sub-scopes
  for (${ownSymbol} s : scope.getSubScopes()) {
    s.accept(getRealThis());
  }