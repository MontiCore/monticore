<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  de.monticore.io.paths.ModelCoordinate mc =
                de.monticore.io.FileFinder.findFile(getModelPath(), modelName, getFileExt(), cache);
  if(null != mc){
    addLoadedFile(mc.getQualifiedPath().toString());
    I${definitionName}ArtifactScope as = symbols2Json.load(mc.getLocation());
    addSubScope(as);
  }