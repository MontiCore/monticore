<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, getFileExt());
  if(mc.isPresent()) && !isFileLoaded(location.get().toString())){
    addLoadedFile(location.get().toString());
    I${definitionName}ArtifactScope as = getSymbols2Json().load(location.get());
    addSubScope(as);
  }
