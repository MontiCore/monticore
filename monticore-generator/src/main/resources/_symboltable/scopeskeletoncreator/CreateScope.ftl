<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "symtabMill", "definitionName")}
  ${scopeInterface} scope = ${symtabMill}.${definitionName?uncap_first}Scope();
  scope.setShadowing(shadowing);
  return scope;