<#-- (c) https://github.com/MontiCore/monticore -->
  if (!this.subScopes.contains(subScope)) {
    this.subScopes.remove(subScope);
    if (subScope.getEnclosingScope() != null && subScope.getEnclosingScope() == this) {
      subScope.setEnclosingScope(null);
    }
  }