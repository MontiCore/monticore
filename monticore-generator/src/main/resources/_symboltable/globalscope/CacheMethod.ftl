<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definitionName")}
  if(this.isPresentModelLoader()){
    if (modelName2ModelLoaderCache.containsKey(calculatedModelName)) {
      modelName2ModelLoaderCache.get(calculatedModelName).add(getModelLoader());
    } else {
      final Set<${definitionName}ModelLoader> ml = new LinkedHashSet<>();
      ml.add(getModelLoader());
      modelName2ModelLoaderCache.put(calculatedModelName, ml);
    }
  }