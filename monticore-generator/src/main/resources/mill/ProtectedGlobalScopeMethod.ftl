<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "globalScope")}
  if(null == ${attributeName}){
    ${attributeName} = new ${globalScope}();
  }
  return ${attributeName};
