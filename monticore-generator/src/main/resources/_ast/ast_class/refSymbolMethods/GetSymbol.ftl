<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName")}
<#assign service = glex.getGlobalVar("service")>
  update${attributeName?cap_first}();
  if (${attributeName} == null) {
    Log.error("0xA7003${service.getGeneratedErrorCode(attributeName + referencedProdName)} ${attributeName} can't return a value. It is empty.");
  }
  return ${attributeName}   ;