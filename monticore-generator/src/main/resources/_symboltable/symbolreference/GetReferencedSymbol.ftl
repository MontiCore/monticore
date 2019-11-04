<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName")}
  if (!isSymbolLoaded()) {
    loadedSymbol = loadSymbol().orElse(null);

    if (!isSymbolLoaded()) {
      Log.error("0xA1038 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
      name + "' (Kind " + "${symbolName}" + ").");
    }
  }

  return loadedSymbol;