<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scope")}
de.monticore.symboltable.serialization.json.JsonObject scope =
  de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
return scopeDeSer.deserializeArtifactScope(scope);
