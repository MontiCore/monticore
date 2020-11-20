<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isScopeSpanningSymbol", "scope")}
  // symbol -> ast
  symbol.setAstNode(ast);

  // ast -> symbol
  ast.setSymbol(symbol);
  ast.setEnclosingScope(symbol.getEnclosingScope());

<#if isScopeSpanningSymbol>
  // ast -> spannedScope
  setLinkBetweenSpannedScopeAndNode((${scope}) symbol.getSpannedScope(), ast);
</#if>