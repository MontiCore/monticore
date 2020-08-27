<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "definitionName")}
  for (String modelName : calculateModelNamesFor${simpleName}(name)) {
    if (!isFileLoaded(modelName)) {
      boolean success = getScopeDeSer().loadSymbolsIntoScope(modelName, getRealThis(), getModelPath());
      if(!success && isPresentModelLoader()){
        getModelLoader().loadModelsIntoScope(modelName, getModelPath(), getRealThis());
      }
      addLoadedFile(modelName);
    } else {
    Log.debug("Already tried to load model for '" + name + "'. If model exists, continue with cached version.",
      "${definitionName}GlobalScope");
    }
  }
