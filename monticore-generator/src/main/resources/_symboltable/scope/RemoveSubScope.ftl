<#-- (c) https://github.com/MontiCore/monticore -->
  this.subScopes.remove(subScope);
if (subScope.getEnclosingScope() != null && subScope.getEnclosingScope() == this) {
    subScope.setEnclosingScope(null);
  }