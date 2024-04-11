<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if(delegate.isPresent()){
    return lazyLoadDelegate().getEnclosingScope();
  }
  return (${scopeName}) enclosingScope;