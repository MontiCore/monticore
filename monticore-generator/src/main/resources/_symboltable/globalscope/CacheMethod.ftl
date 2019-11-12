<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  ${definitionName}ModelLoader modelLoader = this.get${definitionName}Language().getModelLoader() ;
  if (modelName2ModelLoaderCache.containsKey(calculatedModelName)) {
    modelName2ModelLoaderCache.get(calculatedModelName).add(modelLoader);
  } else {
    final Set<${definitionName}ModelLoader> ml = new LinkedHashSet<>();
    ml.add(modelLoader);
    modelName2ModelLoaderCache.put(calculatedModelName, ml);
  }