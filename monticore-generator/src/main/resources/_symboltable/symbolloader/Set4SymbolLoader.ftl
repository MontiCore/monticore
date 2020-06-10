<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
  ${tc.includeArgs("methods.Set", [attribute])}
  this.loadedSymbol = Optional.empty();
  this.isAlreadyLoaded = false;
