<#-- (c) https://github.com/MontiCore/monticore -->
  if (!super.getNameOpt().isPresent()) {
    final Optional<de.monticore.symboltable.ISymbol> topLevelSymbol = getTopLevelSymbol();
    if (topLevelSymbol.isPresent()) {
      setName(topLevelSymbol.get().getName());
    }
  }
  return super.getNameOpt();