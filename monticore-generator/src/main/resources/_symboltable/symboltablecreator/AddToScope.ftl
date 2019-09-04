<#-- (c) https://github.com/MontiCore/monticore -->
  if (!(symbol instanceof de.monticore.symboltable.references.ISymbolReference)) {
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
    }
  }