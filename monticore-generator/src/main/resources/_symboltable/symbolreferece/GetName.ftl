<#-- (c) https://github.com/MontiCore/monticore -->
  if (isReferencedSymbolLoaded()) {
    return getReferencedSymbol().getName();
  }
  return this.name;