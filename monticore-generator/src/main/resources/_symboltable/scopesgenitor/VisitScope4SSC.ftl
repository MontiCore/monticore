<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "simpleScopeName")}
  if (getCurrentScope().isPresent()) {
    node.setEnclosingScope(getCurrentScope().get());
  }
  else {
    Log.error("Could not set enclosing scope of ASTNode \"" + node
      + "\", because no scope is set yet!");
  }
  ${scopeInterface} scope = create_${simpleScopeName}(node);
  putOnStack(scope);
  setLinkBetweenSpannedScopeAndNode(scope, node);