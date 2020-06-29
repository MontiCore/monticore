<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "symbolTableDelegator", "modelLoader", "language", "scopeInterface", "artifactScope",
                "generatedErrorCode", "generatedErrorCode2")}

  if (symbolTableCreator != null) {
    Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
    "${modelLoader}");
    final ${scopeInterface} scope = symbolTableCreator.createFromAST(ast);

    if (!(scope instanceof ${artifactScope})) {
      Log.warn("0xA7001${generatedErrorCode} Top scope of model " + modelName + " is expected to be an artifact scope, but"
                + " is scope \"" + (scope.isPresentName() ? scope.getName() : "") + "\"");
    }

    Log.debug("Created symbol table for model \"" + modelName + "\".", "${modelLoader}");
  }
  else {
    Log.warn("0xA7002${generatedErrorCode2} No symbol created, because '${language}' does not define a symbol table creator.");
  }