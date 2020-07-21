<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList", "superSymbolList")}
    mill = null;
  <#list attributeNameList as attributeName>
    mill${attributeName} = null;
  </#list>
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.reset();
  </#list>
