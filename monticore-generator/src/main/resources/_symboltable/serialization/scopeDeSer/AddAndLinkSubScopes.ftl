<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if (scopeJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES)) {
    List<de.monticore.symboltable.serialization.json.JsonElement> elements = scopeJson.getArrayMember(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES);
    for (de.monticore.symboltable.serialization.json.JsonElement subScopeJson : elements) {
  de.monticore.symboltable.serialization.json.JsonObject s = subScopeJson.getAsJsonObject();
      ${scopeName} subScope = deserialize(s, scope);
      addAndLinkSpanningSymbol(s, subScope, scope);
      subScope.setEnclosingScope(scope);
      scope.addSubScope(subScope);
    }
  }