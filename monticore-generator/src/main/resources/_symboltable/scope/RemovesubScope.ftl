<#-- (c) https://github.com/MontiCore/monticore -->
  this.subScopes.remove(subScope);
  if (subScope.getEnclosingScope().isPresent() && subScope.getEnclosingScope().get() == this) {
    subScope.setEnclosingScope(null);
  }