<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}

  if(checkLazyLoadDelegate()) {
    return lazyLoadDelegate();
  }
  return (${symbolName}) this;
