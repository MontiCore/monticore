<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeSpanningSymbolList")}
  if (subScopeJson.containsKey(de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL)) {
de.monticore.symboltable.serialization.json.JsonObject symbolRef = subScopeJson.get(de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL).getAsJsonObject();
    String spanningSymbolName = symbolRef.get(de.monticore.symboltable.serialization.JsonConstants.NAME).getAsJsonString().getValue();
    String spanningSymbolKind = symbolRef.get(de.monticore.symboltable.serialization.JsonConstants.KIND).getAsJsonString().getValue();
<#list scopeSpanningSymbolList?keys as symbolName>
    if (spanningSymbolKind.equals(${symbolName?uncap_first}DeSer.getSerializedKind())) {
      Optional<${scopeSpanningSymbolList[symbolName]}> spanningSymbol = scope.resolve${symbolName?remove_ending("Symbol")}Locally(spanningSymbolName);
      if (spanningSymbol.isPresent()) {
        subScope.setSpanningSymbol(spanningSymbol.get());
      }
      else {
        Log.error("Spanning symbol of scope "+subScopeJson+" could not be found during deserialization!");
      }
    }
</#list>
  } else {
    Log.error("Unknown kind of scope spanning symbol: "+de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL);
  }