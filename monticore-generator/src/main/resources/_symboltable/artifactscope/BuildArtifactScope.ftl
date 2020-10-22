<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName")}
${scopeClassName} scope;
  if (!enclosingScope.isPresent()) {
    if(null==packageName){
      scope = new ${scopeClassName}();
    }else{
      scope = new ${scopeClassName}(packageName, imports);
    }
  } else {
    scope = new ${scopeClassName}(enclosingScope, packageName, imports);
  }
  return scope;