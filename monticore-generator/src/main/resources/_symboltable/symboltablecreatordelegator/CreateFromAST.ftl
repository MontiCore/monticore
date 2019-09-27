<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  ${artifactScope} as =  symbolTable.createFromAST(rootNode);
  if (as.isPresentName()){
    if (!as.getPackageName().isEmpty()){
      globalScope.cache(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.cache(as.getName());
    }
  }
  return as;