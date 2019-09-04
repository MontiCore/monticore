<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
  // symbol -> ast
  symbol.setAstNode(astNode);

  // ast -> symbol
  astNode.setSymbol(symbol);
  astNode.set${simpleName}Symbol(symbol);
  astNode.setEnclosingScope(symbol.getEnclosingScope());

  // ast -> spannedScope
  astNode.setSpannedScope(symbol.getSpannedScope());