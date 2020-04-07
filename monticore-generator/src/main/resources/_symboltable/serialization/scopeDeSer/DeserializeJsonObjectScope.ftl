<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName")}
  String kind = scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);
  if (this.getSerializedKind().equals(kind)) {
    return deserialize${simpleName}Scope(scopeJson,enclosingScope);
  }
  else if (this.getSerializedASKind().equals(kind)) {
    return deserialize${simpleName}ArtifactScope(scopeJson,enclosingScope);
  }
  Log.error("Cannot deserialize \""+scopeJson+"\" with DeSer for kind \""+this.getSerializedKind()+"\"!");
  return null;