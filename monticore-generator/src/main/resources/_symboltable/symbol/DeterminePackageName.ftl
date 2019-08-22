<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "artifactScope")}
  Optional<? extends ${scopeInterface}> optCurrentScope = Optional.ofNullable(enclosingScope);
  while (optCurrentScope.isPresent()) {
    final ${scopeInterface} currentScope = optCurrentScope.get();
    if (currentScope.isSpannedBySymbol()) {
      // If one of the enclosing scope(s) is spanned by a symbol, take its
      // package name. This check is important, since the package name of the
      // enclosing symbol might be set manually.
      return currentScope.getSpanningSymbol().get().getPackageName();
    } else if (currentScope instanceof AutomataArtifactScope) {
      return ((${artifactScope}) currentScope).getPackageName();
    }
    optCurrentScope = currentScope.getEnclosingScope();
  }
  return "";