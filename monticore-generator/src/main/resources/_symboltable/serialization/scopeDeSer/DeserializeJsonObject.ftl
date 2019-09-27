<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName")}
  String kind = scopeJson.get(de.monticore.symboltable.serialization.JsonConstants.KIND).getAsJsonString().getValue();
  if (this.getSerializedKind().equals(kind)) {
    return Optional.of(deserialize${simpleName}Scope(scopeJson));
  }
  else if (this.getSerializedASKind().equals(kind)) {
    return Optional.of(deserialize${simpleName}ArtifactScope(scopeJson));
  }
  return Optional.empty();