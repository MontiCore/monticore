<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolfullName")}
  ${symbolfullName} symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize(symbolJson, scope);
  scope.add(symbol);