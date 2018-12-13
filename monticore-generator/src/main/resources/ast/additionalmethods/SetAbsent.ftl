<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "isBuilderClass", "isInherited", "hasSymbolReference")}
<#if isInherited>
  super.set${attrName?cap_first}Absent();
<#else>
  ${attrName} = Optional.empty();
</#if>
<#if hasSymbolReference>
  ${attrName}Definition = Optional.empty();
</#if>
<#if isBuilderClass>
  return this;
</#if>