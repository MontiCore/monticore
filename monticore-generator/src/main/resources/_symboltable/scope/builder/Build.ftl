<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName")}
  ${scopeClassName} scope = new ${scopeClassName}(shadowingScope);
  this.spanningSymbol.ifPresent(scope::setSpanningSymbol);
  scope.setExportsSymbols(this.exportsSymbols);
  scope.setEnclosingScopeOpt(this.enclosingScope);
  scope.setSubScopes(this.subScopes);
  scope.setAstNodeOpt(this.astNode);
  this.name.ifPresent(scope::setName);
  this.subScopes.forEach(s -> s.setEnclosingScope(scope));
  return scope;