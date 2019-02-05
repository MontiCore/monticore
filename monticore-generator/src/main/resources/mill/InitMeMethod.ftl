<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList")}
    mill = mill;
  <#list attributeNameList as attributeName>
    mill${attributeName} = mill;
  </#list>