<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName", "isScopeSpanningSymbol")}
  // symbol -> ast
  symbol.setAstNode(ast);

  // ast -> symbol
  ast.setSymbol(symbol);
  ast.set${simpleName}Symbol(symbol);
  ast.setEnclosingScope(symbol.getEnclosingScope());

<#if isScopeSpanningSymbol>
  // ast -> spannedScope
  ast.setSpannedScope(symbol.getSpannedScope());
</#if>