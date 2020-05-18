<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScopeSimpleName")}
  de.monticore.symboltable.serialization.json.JsonObject scope =
    de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
  return deserialize${artifactScopeSimpleName}(scope);