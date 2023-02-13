<#-- (c) https://github.com/MontiCore/monticore -->
  if (!checkLazyLoadDelegate()) {
    return super.determinePackageName();
  }
  return lazyLoadDelegate().determinePackageName();
