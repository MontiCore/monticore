<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClass")}
  // scope -> ast
  scope.setAstNode(astNode);

  // ast -> scope
  astNode.setSpannedScope((${scopeClass}) scope);