<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("errorCode")}
  if (scopeJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
    for (de.monticore.symboltable.serialization.json.JsonElement e : scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
      de.monticore.symboltable.serialization.json.JsonObject symbol = e.getAsJsonObject();
      boolean foundSymbol = false;
      for (de.monticore.symboltable.serialization.json.JsonElement ae : symbol.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.KIND)) {
        String kind = ae.getAsJsonString().getValue();
        foundSymbol = addSymbol(kind, symbol, scope);
        if (foundSymbol) {
          break;
        }
      }
      if (!foundSymbol) {
        Log.error("0x0xA1234${errorCode} Unable to deserialize symbol `" + symbol + "`");
      }
    }
  }