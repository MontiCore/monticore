<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "gloabalScopeInterface", "astStartProd")}
  if (!loadSymbolsIntoScope(qualifiedModelName, modelPath, enclosingScope)) {
    final List<${astStartProd}> asts = loadModels(qualifiedModelName, modelPath);
    for (${astStartProd} ast : asts) {
      createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope);
    }
    showWarningIfParsedModels(asts, qualifiedModelName);
    return asts;
  }
  return new ArrayList<>();