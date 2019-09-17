<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}
  Optional<${symbolName}Symbol> symbol = ${symbolName?uncap_first}SymbolDeSer.deserialize(symbolJson);
  if (symbol.isPresent()) {
    scope.add(symbol.get());
  } else {
    Log.error("Deserialization of " + symbolJson + " failed!");
  }