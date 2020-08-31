<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astStartProd")}
  final List<${astStartProd}> asts = loadModels(qualifiedModelName, modelPath);
  for (${astStartProd} ast : asts) {
    createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope);
  }
  showWarningIfParsedModels(asts, qualifiedModelName);
  return asts;