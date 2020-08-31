<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("errorCode", "errorCode2")}
  // 1. deserialize the map with of the hierarchy of symbol kinds
  java.util.Map<String, String> kindHierarchy = de.monticore.symboltable.serialization.JsonDeSers.deserializeKindHierarchy(scopeJson);

  if (scopeJson.hasArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
    //2. iterate over all stored symbols within the passed scope
    for (de.monticore.symboltable.serialization.json.JsonElement e :
             scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS)) {
      boolean foundSymbol = false;
      if(e.isJsonObject() && e.getAsJsonObject().hasStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND)){
        de.monticore.symboltable.serialization.json.JsonObject symbol = e.getAsJsonObject();
        String kind = symbol.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);
        // begin deserializing the stored symbol with the stored symbol kinds
        do{
          foundSymbol = addSymbol(kind, symbol, scope);
          // if the symbol was found, break the do-while loop
          if (foundSymbol) { break; }
          // otherwise, try to find the parent symbol kind of the current symbol kind, and try this next
          kind = de.monticore.symboltable.serialization.JsonDeSers.getParentKind(kind, kindHierarchy);
        } while(null != kind);
      }
      if (!foundSymbol) {
        Log.error("0xA1234${errorCode} Unable to deserialize symbol `" + e + "`");
      }
    }
  }
  else{
   Log.error("0xA1235${errorCode2} Unable to deserialize symbols in `" + scopeJson + "`");
  }