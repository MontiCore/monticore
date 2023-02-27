<#-- (c) https://github.com/MontiCore/monticore -->
  if (!checkLazyLoadDelegate()) {
    return super.determineFullName();
  }
  return lazyLoadDelegate().determineFullName();
