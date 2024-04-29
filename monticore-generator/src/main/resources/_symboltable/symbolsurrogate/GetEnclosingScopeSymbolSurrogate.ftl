<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if (!checkLazyLoadDelegate()) {
    return (${scopeName}) enclosingScope;
  }
  return lazyLoadDelegate().getEnclosingScope();