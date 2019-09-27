<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}
  String kind = symbolJson.get(de.monticore.symboltable.serialization.JsonConstants.KIND).getAsJsonString().getValue();
  if (this.getSerializedKind().equals(kind)) {
    return Optional.of(deserialize${symbolName}(symbolJson));
  }
  return Optional.empty();