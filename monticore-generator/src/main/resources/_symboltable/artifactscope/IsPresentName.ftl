<#-- (c) https://github.com/MontiCore/monticore -->
  if (!super.isPresentName()) {
    final Optional<de.monticore.symboltable.ISymbol> topLevelSymbol = getTopLevelSymbol();
    topLevelSymbol.ifPresent(iSymbol -> setName(iSymbol.getName()));
  }
  return this.name.isPresent();