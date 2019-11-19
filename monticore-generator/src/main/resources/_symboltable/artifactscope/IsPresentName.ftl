<#-- (c) https://github.com/MontiCore/monticore -->
  if (!super.isPresentName()) {
    final Optional<de.monticore.symboltable.ISymbol> topLevelSymbol = getTopLevelSymbol();
    if (topLevelSymbol.isPresent()) {
      setName(topLevelSymbol.get().getName());
    }
  }
  return this.name.isPresent();