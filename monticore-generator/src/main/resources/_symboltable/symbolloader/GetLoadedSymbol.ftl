<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName")}
  if (!isAlreadyLoaded) {
    loadedSymbol = loadSymbol();
  }

  if (!loadedSymbol.isPresent()) {
    Log.error("0xA1038 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
    name + "' (Kind " + "${symbolName}" + ").");
  }

  return loadedSymbol.get();