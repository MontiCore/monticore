<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if (scopeJson.containsMember(de.monticore.symboltable.serialization.JsonConstants.SUBSCOPES)) {
    List<de.monticore.symboltable.serialization.json.JsonElement> elements = scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonConstants.SUBSCOPES);
    for (de.monticore.symboltable.serialization.json.JsonElement subScopeJson : elements) {
  de.monticore.symboltable.serialization.json.JsonObject s = subScopeJson.getAsJsonObject();
      ${scopeName} subScope = deserialize(s);
      if (null!=subScope) {
        addAndLinkSpanningSymbol(s, subScope, scope);
        subScope.setEnclosingScope(scope);
        scope.addSubScope(subScope);
      }
      else {
        Log.error("Deserialization of subscope "+s+" failed!");
      }
    }
  }