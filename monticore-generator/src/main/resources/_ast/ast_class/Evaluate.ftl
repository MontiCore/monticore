<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass", "interpreterName")}
<#assign service = glex.getGlobalVar("service")>
<#assign plainName = astcdClass.getName()?remove_ending("TOP")>
  if (interpreter instanceof ${interpreterName}) {
    return evaluate((${interpreterName})interpreter);
  } else {
    throw new UnsupportedOperationException("0xA7011${service.getGeneratedErrorCode(astcdClass.getName())} Only handwritten class ${plainName} is supported for the visitor");
  }