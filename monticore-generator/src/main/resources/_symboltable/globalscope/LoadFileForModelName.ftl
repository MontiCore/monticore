<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  String symbolFileExtension = getFileExt() + "sym";
  de.monticore.io.paths.ModelCoordinate modelCoordinate =
     de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate(modelName, symbolFileExtension);
  String filePath = modelCoordinate.getQualifiedPath().toString();
  if(!isFileLoaded(filePath)) {

    //Load symbol table into enclosing global scope if a file has been found
    getModelPath().resolveModel(modelCoordinate);
    if (modelCoordinate.hasLocation()) {
      java.net.URL url = modelCoordinate.getLocation();
      this.addSubScope(scopeDeSer.load(url));
    }
    addLoadedFile(filePath);
  } else {
    Log.debug("Already tried to load model for '" + symbolName + "'. If model exists, continue with cached version.",
      "${definitionName}GlobalScope");
  }