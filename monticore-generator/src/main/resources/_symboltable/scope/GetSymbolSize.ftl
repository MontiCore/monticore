<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolAttributeNameList")}
    return
<#list symbolAttributeNameList as attrName>
  ${attrName}.size() <#if !attrName?is_last> + </#if>
</#list>
  ;