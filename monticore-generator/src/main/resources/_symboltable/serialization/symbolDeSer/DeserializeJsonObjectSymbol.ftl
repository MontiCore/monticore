<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}
  if (de.monticore.symboltable.serialization.JsonDeSers.isCorrectDeSerForKind(this, symbolJson)) {
    return deserialize${symbolName}(symbolJson,enclosingScope);
  }
  else {
    Log.error("Unable to deserialize \""+symbolJson+"\" with DeSer for kind "+this.getSerializedKind());
  }
  return null;