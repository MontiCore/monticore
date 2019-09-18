<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolfullName")}
  Optional<${symbolfullName}> symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize(symbolJson);
  if (symbol.isPresent()) {
    scope.add(symbol.get());
  } else {
    Log.error("Deserialization of " + symbolJson + " failed!");
  }