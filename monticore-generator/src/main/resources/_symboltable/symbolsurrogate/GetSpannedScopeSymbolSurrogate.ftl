<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
  if (!checkLazyLoadDelegate()) {
    return (${scopeName}) spannedScope;
  }
  return lazyLoadDelegate().getSpannedScope();
