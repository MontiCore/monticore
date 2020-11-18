<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symtabMill", "artifactScope", "symboltableCreator", "generatedErrorCode")}
  Log.errorIfNull(rootNode, "0xA7004${generatedErrorCode} Error by creating of the ${symboltableCreator} symbol table: top ast node is null");
  I${artifactScope} artifactScope = ${symtabMill}.${artifactScope?uncap_first}();
  artifactScope.setPackageName("");
  artifactScope.setImportsList(new ArrayList<>());
  putOnStack(artifactScope);
  rootNode.accept(getRealThis());
  return artifactScope;