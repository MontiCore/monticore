<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "isScopeSpanningSymbol")}
  addToScope(symbol);
<#if isScopeSpanningSymbol>
  ${scopeInterface} scope = createScope(false);
  putOnStack(scope);
  symbol.setSpannedScope(scope);
</#if>
  setLinkBetweenSymbolAndNode(symbol, ast);