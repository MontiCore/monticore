<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "definitionName")}
  if(isPresentModelLoader()) {
    for (String calculatedModelName : calculateModelNamesFor${simpleName}(name)) {
      if (continueWithModelLoader(calculatedModelName, getModelLoader())) {
        getModelLoader().loadModelsIntoScope(calculatedModelName, getModelPath(), getRealThis());
        cache(calculatedModelNames.iterator().next());
      } else {
        Log.debug("Already tried to load model for '" + name + "'. If model exists, continue with cached version.",
          "${definitionName}GlobalScope");
      }
    }
  }