<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName")}
  if (!isReferencedSymbolLoaded()) {
    referencedSymbol = loadReferencedSymbol().orElse(null);

    if (!isReferencedSymbolLoaded()) {
      Log.error("0xA1038 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
      name + "' (Kind " + "${symbolName}" + ").");
    }
  }

  return referencedSymbol;