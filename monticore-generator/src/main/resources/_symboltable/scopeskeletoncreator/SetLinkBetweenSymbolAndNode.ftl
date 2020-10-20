<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isScopeSpanningSymbol", "artifactScope")}
  // symbol -> ast
  symbol.setAstNode(ast);

  // ast -> symbol
  ast.setSymbol(symbol);
  ast.setEnclosingScope(symbol.getEnclosingScope());

<#if isScopeSpanningSymbol>
  // ast -> spannedScope
  setLinkBetweenSpannedScopeAndNode((${artifactScope}) symbol.getSpannedScope(), ast);
</#if>