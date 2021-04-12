<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName")}
<#assign service = glex.getGlobalVar("service")>
  update${attributeName?cap_first}();
  if (${attributeName} == null) {
    Log.error("0xA7303${service.getGeneratedErrorCode(attributeName + referencedProdName)} get for ${attributeName} can't return a value. Attribute is empty.");
  }
  return ${attributeName}   ;
