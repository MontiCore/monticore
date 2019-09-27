<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "symbolTableDelegator", "modelLoader", "scopeInterface", "artifactScope")}
  final ${symbolTableDelegator} symbolTableCreator =
        getModelingLanguage().getSymbolTableCreator(enclosingScope);

  if (symbolTableCreator != null) {
    Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
    ${modelLoader}.class.getSimpleName());
    final ${scopeInterface} scope = symbolTableCreator.createFromAST(ast);

    if (!(scope instanceof ${artifactScope})) {
      Log.warn("0xA7001x050 Top scope of model " + modelName + " is expected to be an artifact scope, but"
                + " is scope \"" + scope.getName() + "\"");
    }

    Log.debug("Created symbol table for model \"" + modelName + "\".", ${modelLoader}.class.getSimpleName());
  }
  else {
    Log.warn("0xA7002x050 No symbol created, because '" + getModelingLanguage().getName()
              + "' does not define a symbol table creator.");
  }