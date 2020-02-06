<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope", "symboltableCreator", "generatedErrorCode")}
  Log.errorIfNull(rootNode, "0xA7004${generatedErrorCode} Error by creating of the ${symboltableCreator} symbol table: top ast node is null");
  ${artifactScope} artifactScope = new ${artifactScope}(Optional.empty(), "", new ArrayList<>());
  putOnStack(artifactScope);
  rootNode.accept(getRealThis());
  return artifactScope;