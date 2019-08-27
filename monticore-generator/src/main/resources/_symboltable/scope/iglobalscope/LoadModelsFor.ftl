<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "definitionName")}
  ${definitionName}ModelLoader modelLoader = get${definitionName}Language().getModelLoader();
  Set<String> calculatedModelNames = get${definitionName}Language().calculateModelNamesFor${simpleName}(name);

  for (String calculatedModelName : calculatedModelNames) {
    if (continueWithModelLoader(calculatedModelName, modelLoader)) {
      modelLoader.loadModelsIntoScope(calculatedModelName, getModelPath(), getRealThis());
      cache(calculatedModelNames.iterator().next());
    } else {
      Log.debug("Already tried to load model for '" + name + "'. If model exists, continue with cached version.",
        ${definitionName}GlobalScope.class.getSimpleName());
    }
  }