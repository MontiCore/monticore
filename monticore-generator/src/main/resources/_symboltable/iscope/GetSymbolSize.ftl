<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolAttributeNameList")}
    return
<#list symbolAttributeNameList as attrName>
    get${attrName?cap_first}().size() <#if !attrName?is_last> + </#if>
</#list>
  ;