<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "isScopeSpanningSymbol", "isShadowing", "isNonExporting")}
  addToScope(symbol);
<#if isScopeSpanningSymbol>
    ${scopeInterface} scope = createScope(<#if isShadowing>true<#else>false</#if>);
    <#if isNonExporting>
      scope.setExportingSymbols(false);
    </#if>
  putOnStack(scope);
  symbol.setSpannedScope(scope);
</#if>
  setLinkBetweenSymbolAndNode(symbol, ast);