<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList")}
    mill = null;
  <#list attributeNameList as attributeName>
    mill${attributeName} = null;
  </#list>
