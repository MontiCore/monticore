<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superSymbolList")}
    mill = null;
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.reset();
  </#list>
