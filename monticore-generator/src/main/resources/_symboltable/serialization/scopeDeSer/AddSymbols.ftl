<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("errorCode", "errorCode2")}
  if (scopeJson.hasArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
    for (de.monticore.symboltable.serialization.json.JsonElement e :
             scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
      boolean foundSymbol = false;
      if(e.isJsonObject()){
        de.monticore.symboltable.serialization.json.JsonObject symbol = e.getAsJsonObject();
        if(symbol.hasArrayMember(de.monticore.symboltable.serialization.JsonDeSers.KIND)) {
          for (de.monticore.symboltable.serialization.json.JsonElement ae :
                symbol.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.KIND)) {
            String kind = ae.getAsJsonString().getValue();
            foundSymbol = addSymbol(kind, symbol, scope);
            if (foundSymbol) {
              break;
            }
          }
        }
      }
      if (!foundSymbol) {
        Log.error("0xA1234${errorCode} Unable to deserialize symbol `" + e + "`");
      }
    }
  }
  else{
   Log.error("0xA1235${errorCode2} Unable to deserialize symbols in `" + scopeJson + "`");
  }