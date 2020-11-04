<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  ${artifactScope} as =  symbolTable.createFromAST(rootNode);
  if (as.isPresentName()){
    if (!as.getPackageName().isEmpty()){
      globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.addLoadedFile(as.getName());
    }
  }
  return as;