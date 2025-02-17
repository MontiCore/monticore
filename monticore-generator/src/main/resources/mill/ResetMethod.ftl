<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeNameList", "superSymbolList")}
    mill.remove();
  <#list attributeNameList as attributeName>
    mill${attributeName}.remove();
  </#list>
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.reset();
  </#list>
