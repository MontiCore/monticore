<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName")}
  ${scopeClassName} scope = new ${scopeClassName}(shadowing);
  this.spanningSymbol.ifPresent(scope::setSpanningSymbol);
  scope.setExportingSymbols(this.exportingSymbols);
scope.setEnclosingScope(this.enclosingScope);
  scope.setSubScopes(this.subScopes);
  if (this.isPresentAstNode()) {
    scope.setAstNode(this.getAstNode());
  } else {
    scope.setAstNodeAbsent();
  }
  this.name.ifPresent(scope::setName);
  this.subScopes.forEach(s -> s.setEnclosingScope(scope));
  return scope;