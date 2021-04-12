<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
de.monticore.symboltable.IScope optCurrentScope = enclosingScope;
while (optCurrentScope != null) {
final de.monticore.symboltable.IScope currentScope = optCurrentScope;
    if (currentScope.isPresentSpanningSymbol()) {
      // If one of the enclosing scope(s) is spanned by a symbol, take its
      // package name. This check is important, since the package name of the
      // enclosing symbol might be set manually.
      return currentScope.getSpanningSymbol().getPackageName();
    } else if (currentScope instanceof de.monticore.symboltable.IArtifactScope) {
      return ((de.monticore.symboltable.IArtifactScope) currentScope).getPackageName();
    }
optCurrentScope = currentScope.getEnclosingScope();
  }
  return "";