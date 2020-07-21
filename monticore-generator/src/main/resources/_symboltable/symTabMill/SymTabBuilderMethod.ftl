<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
  if (${attributeName} == null) {
    ${attributeName} = getMill();
  }
  return ${attributeName}._${attributeName}Builder();