<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}
  de.monticore.symboltable.serialization.json.JsonObject symbol =
    de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
  return deserialize${symbolName}(symbol, enclosingScope);