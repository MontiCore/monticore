<#-- (c) https://github.com/MontiCore/monticore -->
  if (!this.subScopes.contains(subScope)) {
    this.subScopes.add(subScope);
    subScope.setEnclosingScope(this);
  }