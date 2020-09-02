<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  if(!isFileLoaded(modelName)) {
    boolean success = false;

    //1.Load symbol table into enclosing global scope if a file has been found
    de.monticore.io.paths.ModelCoordinate modelCoordinate = getModelCoordinate(modelName, getSymbolFileExtension(), getModelPath());
    if (modelCoordinate.hasLocation()) {
      java.net.URL url = modelCoordinate.getLocation();
      this.addSubScope(scopeDeSer.load(url));
      success = true;
    }
    //2. If no symbol was found with deser, try to load model with modelloader
    if(!success && isPresentModelLoader()){
      getModelLoader().loadModelsIntoScope(modelName, getModelPath(), getRealThis());
    }
    addLoadedFile(modelName);
  } else {
    Log.debug("Already tried to load model for '" + symbolName + "'. If model exists, continue with cached version.",
      "${definitionName}GlobalScope");
  }