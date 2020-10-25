<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName", "hasOptionalName")}
<#if hasOptionalName>
  if (node.isPresentName()) {
</#if>
    ${symbolName} symbol = create_${simpleName}(node);
    addToScopeAndLinkWithNode(symbol, node);
<#if hasOptionalName>
  } else {
    if (getCurrentScope().isPresent()) {
      node.setEnclosingScope(getCurrentScope().get());
    } else {
      Log.error("Could not set enclosing scope of ASTNode \"" + node
        + "\", because no scope is set yet!");
    }
  }
</#if>