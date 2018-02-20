<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list ast.getCDParameterList() as checkedParameter>
  <#assign type = checkedParameter.getType()>
  <#if genHelper.isNullable(type) && !genHelper.isOptional(type)>
    // MontiCore generally assumes that null is not used, but if you are
    // unsure then override template ast.ErrorIfNull
    // Log.errorIfNull(${genHelper.getJavaAndCdConformName(checkedParameter.getName())}, "0xA7006${genHelper.getGeneratedErrorCode(checkedParameter)} Parameter '${checkedParameter.getName()}' must not be null.");
  </#if>
</#list> 
