<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  boolean hasSymbolsInSubScopes = false;
  for (${scopeInterface} subScope : scope.getSubScopes()) {
    hasSymbolsInSubScopes |= hasSymbolsInSubScopes(subScope);
  if (hasSymbolsInSubScopes) return true;
  }
  return hasSymbolsInSubScopes | scope.getSymbolsSize()>0;