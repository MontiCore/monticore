<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  // scope -> ast
  scope.setAstNode(ast);

  // ast -> scope
  ast.setSpannedScope((${scopeInterface}) scope);