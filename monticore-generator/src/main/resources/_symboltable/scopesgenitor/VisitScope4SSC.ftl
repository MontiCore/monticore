<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "simpleScopeName", "isShadowing", "isNonExporting", "isOrdered")}
  if (getCurrentScope().isPresent()) {
    node.setEnclosingScope(getCurrentScope().get());
  }
  else {
    Log.error("Could not set enclosing scope of ASTNode \"" + node
      + "\", because no scope is set yet!");
  }
  ${scopeInterface} scope = createScope(<#if isShadowing>true<#else>false</#if>);
  <#if isNonExporting>
  scope.setExportingSymbols(false);
  </#if>
  <#if isOrdered>
  scope.setOrdered(true);
  </#if>
  putOnStack(scope);
  // scope -> ast
  scope.setAstNode(node);

  // ast -> scope
  node.setSpannedScope(scope);
  initScopeHP1(scope);