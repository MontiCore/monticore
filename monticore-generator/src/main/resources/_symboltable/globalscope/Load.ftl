<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName")}
  for (String modelName : calculateModelNamesFor${simpleName}(name)) {
    loadFileForModelName(modelName);
  }
