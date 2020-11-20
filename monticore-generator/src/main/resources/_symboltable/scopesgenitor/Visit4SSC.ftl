<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName")}
  ${symbolName} symbol = create_${simpleName}(node).build();
  addToScopeAndLinkWithNode(symbol, node);