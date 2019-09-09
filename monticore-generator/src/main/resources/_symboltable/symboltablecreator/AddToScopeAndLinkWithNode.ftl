<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  addToScope(symbol);
  setLinkBetweenSymbolAndNode(symbol, ast);
  ${scopeInterface} scope = createScope(false);
  putOnStack(scope);
  symbol.setSpannedScope(scope);