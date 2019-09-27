<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  boolean hasSymbolsInSubScopes = false;
  for (${scopeInterface} subScope : scope.getSubScopes()) {
    hasSymbolsInSubScopes |= hasSymbolsInSubScopes(subScope);
  }
  return hasSymbolsInSubScopes | scope.getSymbolsSize()>0;