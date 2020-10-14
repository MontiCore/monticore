<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
  if(null == ${attributeName}){
    ${attributeName} = ${attributeName}Builder().build();
  }
  return ${attributeName};
