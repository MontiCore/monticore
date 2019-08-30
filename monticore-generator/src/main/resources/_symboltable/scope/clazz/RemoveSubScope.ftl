<#-- (c) https://github.com/MontiCore/monticore -->
  this.subScopes.remove(subScope);
  if (subScope.isPresentEnclosingScope() && subScope.getEnclosingScope() == this) {
    subScope.setEnclosingScope(null);
  }