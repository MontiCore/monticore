<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if genHelper.isSupertypeOfHWType(astType.getName())>
  <#assign plainName = genHelper.getPlainName(astType)>
    // We allow a down cast here, because the subclass ${plainName} must exist
    // and only this subclass may exist in the AST and hence, only this class may
    // be handled by a visitor. All other cases are invalid an throw an exception!
    // This decision was made during MC Sprint Review on 16.03.2015.
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
    }
<#else>
      visitor.handle(this);
</#if>
