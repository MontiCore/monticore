<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName", "resolvingDelegates")}
  ${scopeClassName} globalScope = new ${scopeClassName}(modelPath, modelFileExtension);
  if(modelLoader.isPresent()){
    globalScope.setModelLoader(modelLoader.get());
  }
  else {
    globalScope.setModelLoaderAbsent();
  }
<#list resolvingDelegates as resolvingDelegate>
  globalScope.set${resolvingDelegate?cap_first}List(${resolvingDelegate});
</#list>
  return globalScope;