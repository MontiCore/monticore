<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolfullName")}
  ${symbolfullName} symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize(symbolJson);
  if (null!=symbol) {
    scope.add(symbol);
  } else {
    Log.error("Deserialization of " + symbolJson + " failed!");
  }