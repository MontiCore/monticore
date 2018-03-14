<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attrName", "isBuilderClass", "isInherited")}
<#if isInherited>
  super.set${attrName?cap_first}Opt(value);
<#else>
  this.${attrName} = value;
</#if>
<#if isBuilderClass>
  return this;
</#if>