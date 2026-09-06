<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}

  if(checkLazyLoadDelegate()) {
    if(!(obj instanceof ${symbolName})) {
      return false;
    }

    return lazyLoadDelegate().equals(((${symbolName}) obj).getThis());
  }

  return super.equals(obj);