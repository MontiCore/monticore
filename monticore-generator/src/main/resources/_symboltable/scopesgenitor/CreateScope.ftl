<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "symtabMill")}
  ${scopeInterface} scope = ${symtabMill}.scope();
  scope.setShadowing(shadowing);
  return scope;