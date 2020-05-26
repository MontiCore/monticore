<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName", "simpleScopeName")}
  if (scopeJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES)) {
    List<de.monticore.symboltable.serialization.json.JsonElement> elements = scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES);
    for (de.monticore.symboltable.serialization.json.JsonElement subScopeJson : elements) {
  de.monticore.symboltable.serialization.json.JsonObject s = subScopeJson.getAsJsonObject();
      ${scopeName} subScope = deserialize${simpleScopeName}(s);
      addAndLinkSpanningSymbol(s, subScope, scope);
      scope.addSubScope(subScope);
    }
  }