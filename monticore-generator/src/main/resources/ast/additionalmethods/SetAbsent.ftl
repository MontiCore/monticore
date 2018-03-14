<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attrName", "isBuilderClass", "isInherited")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if isInherited>
  super.set${attrName?cap_first}Absent();
<#else>
  ${attrName} = Optional.empty();
</#if>
<#if isBuilderClass>
  return this;
</#if>