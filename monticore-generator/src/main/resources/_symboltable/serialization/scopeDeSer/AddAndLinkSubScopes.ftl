<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if (scopeJson.containsKey(de.monticore.symboltable.serialization.JsonConstants.SUBSCOPES)) {
    List<de.monticore.symboltable.serialization.json.JsonElement> elements = scopeJson.get(de.monticore.symboltable.serialization.JsonConstants.SUBSCOPES).getAsJsonArray().getValues();
    for (de.monticore.symboltable.serialization.json.JsonElement subScopeJson : elements) {
  de.monticore.symboltable.serialization.json.JsonObject s = subScopeJson.getAsJsonObject();
      Optional<${scopeName}> subScope = deserialize(s);
      if (subScope.isPresent()) {
        addAndLinkSpanningSymbol(s, subScope.get(), scope);
        subScope.get().setEnclosingScope(scope);
        scope.addSubScope(subScope.get());
      }
      else {
        Log.error("Deserialization of subscope "+s+" failed!");
      }
    }
  }