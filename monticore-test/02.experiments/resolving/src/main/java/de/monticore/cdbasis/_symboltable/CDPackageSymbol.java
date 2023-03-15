/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;

public class CDPackageSymbol extends CDPackageSymbolTOP {

  public CDPackageSymbol(String name) {
    super(name);
  }

  @Override
  protected String determinePackageName() {
    IScope optCurrentScope = enclosingScope;
    while (optCurrentScope != null) {
      final de.monticore.symboltable.IScope currentScope = optCurrentScope;
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().getPackageName();
      } else if (currentScope instanceof IArtifactScope) {
        return ((IArtifactScope) currentScope).getFullName();
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }
    return "";
  }

  public String getInternalQualifiedName() {
    String internalName = getFullName();
    IScope as = getEnclosingScope();
    while (!(as instanceof IArtifactScope)) {
      as = as.getEnclosingScope();
    }
    String artifactName = ((IArtifactScope) as).getFullName();
    if (!artifactName.isEmpty()) {
      internalName = internalName.substring(artifactName.length() + 1);
    }
    return internalName;
  }
}

