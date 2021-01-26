<#-- (c) https://github.com/MontiCore/monticore -->
  de.monticore.symboltable.serialization.json.JsonObject scope =
    de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
  return deserializeArtifactScope(scope);