<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symtabMill", "artifactScope", "symboltableCreator", "generatedErrorCode")}
  Log.errorIfNull(rootNode, "0xA7004${generatedErrorCode} Error by creating of the ${symboltableCreator} symbol table: top ast node is null");
  I${artifactScope} artifactScope = ${symtabMill}.artifactScope();
  artifactScope.setPackageName("");
  artifactScope.setImportsList(new ArrayList<>());
  artifactScope.setAstNode(rootNode);
  putOnStack(artifactScope);
  initArtifactScopeHP1(artifactScope);
  rootNode.accept(getTraverser());
  initArtifactScopeHP2(artifactScope);
  scopeStack.remove(artifactScope);
  return artifactScope;