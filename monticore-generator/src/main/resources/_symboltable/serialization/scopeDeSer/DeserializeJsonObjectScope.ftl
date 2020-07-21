<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "artifactScopeName", "scopeDeSerName")}
  String kind = scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);
  if ("${artifactScopeName}".equals(kind)) {
    return deserialize${simpleName}ArtifactScope(scopeJson);
  }
  Log.error("Cannot deserialize \""+kind+"\" with a ${scopeDeSerName}!");
  return null;