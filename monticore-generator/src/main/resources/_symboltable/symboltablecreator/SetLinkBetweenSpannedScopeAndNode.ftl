<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClass")}
  // scope -> ast
  scope.setAstNode(ast);

  // ast -> scope
  ast.setSpannedScope((${scopeClass}) scope);