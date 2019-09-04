<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName")}
  ${symbolName} symbol = create_${simpleName}(ast);
  initialize_${simpleName}(symbol, ast);
  addToScopeAndLinkWithNode(symbol, ast);