<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeSpanningSymbolList")}
  if (subScopeJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SCOPE_SPANNING_SYMBOL)) {
de.monticore.symboltable.serialization.json.JsonObject symbolRef = subScopeJson.getObjectMember(de.monticore.symboltable.serialization.JsonDeSers.SCOPE_SPANNING_SYMBOL);
    String spanningSymbolName = symbolRef.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME);
    String spanningSymbolKind = symbolRef.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);
<#assign elifStmnt="if">
<#list scopeSpanningSymbolList?keys as symbolName>
    ${elifStmnt} (spanningSymbolKind.equals(${symbolName?uncap_first}DeSer.getSerializedKind())) {
      Optional<${scopeSpanningSymbolList[symbolName]}> spanningSymbol = scope.resolve${symbolName?remove_ending("Symbol")}Locally(spanningSymbolName);
      if (spanningSymbol.isPresent()) {
        spanningSymbol.get().setSpannedScope(subScope);
      }
      else {
        Log.error("Spanning symbol of scope "+subScopeJson+" could not be found during deserialization!");
      }
    }<#assign elifStmnt="else if">
</#list>
  else {
    Log.error("Unknown kind of scope spanning symbol: "+spanningSymbolKind);
  }
}