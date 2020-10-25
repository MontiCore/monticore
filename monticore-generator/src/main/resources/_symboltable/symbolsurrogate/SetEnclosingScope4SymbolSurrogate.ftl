<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "scopeName")}
  ${tc.includeArgs("methods.Set", [attribute])}
  if(delegate.isPresent()){
    lazyLoadDelegate().setEnclosingScope((${scopeName}) enclosingScope);
  }
