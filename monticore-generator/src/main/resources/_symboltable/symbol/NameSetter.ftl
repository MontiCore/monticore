<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName")}
  if (${attrName} == null) {
    ${attrName} = determine${attrName?cap_first}();
  }
  return ${attrName};