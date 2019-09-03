<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "symtabMill", "simlpeName")}
  ${scopeInterface} scope = ${symtabMill}.${simpleName?uncap_first}ScopeBuilder().build();
  scope.setShadowing(shadowing);
  return scope;