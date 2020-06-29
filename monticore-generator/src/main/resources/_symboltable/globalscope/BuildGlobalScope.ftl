<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName", "resolvingDelegates", "generatedErrorCode", "generatedErrorCode2")}
  if(null == modelPath ){
    Log.error("0xA7007${generatedErrorCode} Cannot build a global scope without setting the model path!");
  }
  if(null == modelFileExtension){
    Log.error("0xA7008${generatedErrorCode2} Cannot build a global scope without setting a model file extension!");
  }
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