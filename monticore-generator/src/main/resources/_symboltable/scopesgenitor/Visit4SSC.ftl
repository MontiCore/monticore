<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleSymbolName", "simpleName", "scopeInterface", "hasOptionalName", "isScopeSpanning", "isShadowing", "isNonExporting", "isOrdered", "errorCode", "millFullName")}
<#if hasOptionalName>
  if (node.isPresentName()) {
</#if>
  ${symbolName} symbol = ${millFullName}.${simpleSymbolName?uncap_first}SymbolBuilder().setName(node.getName()).build();
  if (getCurrentScope().isPresent()) {
    getCurrentScope().get().add(symbol);
  } else {
    Log.warn("0xA5021${errorCode} Symbol cannot be added to current scope, since no scope exists.");
  }
<#if isScopeSpanning>
  ${scopeInterface} scope = createScope(<#if isShadowing>true<#else>false</#if>);
  <#if isNonExporting>
  scope.setExportingSymbols(false);
  </#if>
  <#if isOrdered>
  scope.setOrdered(true);
  </#if>
  putOnStack(scope);
  symbol.setSpannedScope(scope);
</#if>
  // symbol -> ast
  symbol.setAstNode(node);

  // ast -> symbol
  node.setSymbol(symbol);
  node.setEnclosingScope(symbol.getEnclosingScope());

  <#if isScopeSpanning>
  // scope -> ast
  scope.setAstNode(node);

  // ast -> scope
  node.setSpannedScope(scope);
  initScopeHP1(scope);
</#if>
  init${simpleSymbolName}HP1(node.getSymbol());
<#if hasOptionalName>
  } else {
    ${tc.include("_symboltable.scopesgenitor.VisitNoSymbol")}
  }
</#if>