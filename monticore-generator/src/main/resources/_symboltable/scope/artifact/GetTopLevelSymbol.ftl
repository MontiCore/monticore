<#-- (c) https://github.com/MontiCore/monticore -->
  if (getSubScopes().size() == 1) {
    if(getSubScopes().get(0).isPresentSpanningSymbol()) {
      ISymbol scopeSpanningSymbol = getSubScopes().get(0).getSpanningSymbol();
      return Optional.of(scopeSpanningSymbol);
    }
  }
  // there is no top level symbol, if more than one sub scope exists.
  return Optional.empty();