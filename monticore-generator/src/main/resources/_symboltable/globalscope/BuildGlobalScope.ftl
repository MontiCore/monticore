<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName")}
  if(modelLoader.isPresent()){
    return new ${scopeClassName}(modelPath, modelLoader.get());
  }
  return new ${scopeClassName}(modelPath);