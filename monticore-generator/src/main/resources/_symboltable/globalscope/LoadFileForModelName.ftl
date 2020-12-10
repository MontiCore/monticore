<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  java.util.Optional<de.monticore.io.paths.ModelCoordinate> mc =
  de.monticore.io.FileFinder.findFile(getModelPath(), modelName, getFileExt(), cache);
  if(mc.isPresent()){
    addLoadedFile(mc.get().getQualifiedPath().toString());
    I${definitionName}ArtifactScope as = symbols2Json.load(mc.get().getLocation());
    addSubScope(as);
  }