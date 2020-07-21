<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList")}
    mill = a;
  <#list attributeNameList as attributeName>
    mill${attributeName} = a;
  </#list>