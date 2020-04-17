<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolfullName")}
  ${symbolfullName} symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize${symbolSimpleName}(symbolJson, scope);
  scope.add(symbol);