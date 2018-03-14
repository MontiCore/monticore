<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("parameters")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list parameters as checkedParameter>
  <#assign type = checkedParameter.getType()>
  <#if genHelper.isNullable(type)>
    // MontiCore generally assumes that "null" is not used, but if you are
    // unsure override template factorymethods.ErrorIfNull
    //  Log.errorIfNull(${genHelper.getJavaAndCdConformName(checkedParameter.getName())}, "0xA7007${genHelper.getGeneratedErrorCode(checkedParameter)} Parameter '${checkedParameter.getName()}' must not be null.");
  </#if> 
</#list> 
