<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName")}
  ${scopeClassName} globalScope = new ${scopeClassName}(modelPath);
  if(modelLoader.isPresent()){
    globalScope.setModelLoader(modelLoader.get());
  }
  return globalScope;