<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  addToScope(symbol);
  setLinkBetweenSymbolAndNode(symbol, astNode);
  ${scopeInterface} scope = createScope(false);
  putOnStack(scope);
  symbol.setSpannedScope(scope);