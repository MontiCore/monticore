<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClassName", "simpleName")}
  ${scopeClassName} scope = new ${scopeClassName}(modelPath, ${simpleName?uncap_first}Language);
  return scope;