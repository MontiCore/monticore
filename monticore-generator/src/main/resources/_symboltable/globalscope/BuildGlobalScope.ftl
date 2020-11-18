<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName", "resolvingDelegates")}
  if(null == modelPath ){
    modelPath = new de.monticore.io.paths.ModelPath();
  }
  if(null == modelFileExtension){
    modelFileExtension = "";
  }
  ${scopeClassName} globalScope = new ${scopeClassName}(modelPath, modelFileExtension);
<#list resolvingDelegates as resolvingDelegate>
  globalScope.set${resolvingDelegate?cap_first}List(${resolvingDelegate});
</#list>
  return globalScope;