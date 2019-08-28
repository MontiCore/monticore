<#-- (c) https://github.com/MontiCore/monticore -->
  if (getSubScopes().size() == 1) {
    if(getSubScopes().get(0).isSpannedBySymbol()) {
      ISymbol scopeSpanningSymbol = getSubScopes().get(0).getSpanningSymbol().get();
      return Optional.of(scopeSpanningSymbol);
    }
  }
  // there is no top level symbol, if more than one sub scope exists.
  return Optional.empty();