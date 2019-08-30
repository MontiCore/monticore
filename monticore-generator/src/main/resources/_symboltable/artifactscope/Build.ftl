<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName")}
${scopeClassName} scope;
  if (enclosingScope.isPresent()) {
    scope = new ${scopeClassName}(packageName, imports);
  } else {
    scope = new ${scopeClassName}(enclosingScope, packageName, imports);
  }
  return scope;