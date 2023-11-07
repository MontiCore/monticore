<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass")}
<#assign service = glex.getGlobalVar("service")>
<#if astcdClass.getName()?ends_with("TOP")>
  <#assign plainName = astcdClass.getName()?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      return interpreter.interpret((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7011${service.getGeneratedErrorCode(astcdClass.getName())} Only handwritten class ${plainName} is supported for the visitor");
    }
<#else>
  return interpreter.interpret(this);
</#if>