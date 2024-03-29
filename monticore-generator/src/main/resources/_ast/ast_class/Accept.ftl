<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass")}
<#assign service = glex.getGlobalVar("service")>
<#if astcdClass.getName()?ends_with("TOP")>
  <#assign plainName = astcdClass.getName()?remove_ending("TOP")>
    // We allow a down cast here, because the subclass ${plainName} must exist
    // and only this subclass may exist in the AST and hence, only this class may
    // be handled by a visitor. All other cases are invalid an throw an exception!
    // This decision was made during MC Sprint Review on 16.03.2015.
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7011${service.getGeneratedErrorCode(astcdClass.getName())} Only handwritten class ${plainName} is supported for the visitor");
    }
<#else>
      visitor.handle(this);
</#if>