<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList")}
    mill.set(a);
  <#list attributeNameList as attributeName>
    mill${attributeName}.set(a);
  </#list>