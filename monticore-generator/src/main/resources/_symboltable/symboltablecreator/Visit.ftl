<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName")}
  ${symbolName} symbol = create_${simpleName}(node);
  initialize_${simpleName}(symbol, node);
  addToScopeAndLinkWithNode(symbol, node);