<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
  ${tc.includeArgs("methods.Set", [attribute])}
  if(delegate.isPresent()){
    lazyLoadDelegate().set${attribute.getName()?cap_first}(${attribute.getName()});
  }
