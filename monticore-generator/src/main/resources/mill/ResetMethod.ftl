<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superSymbolList")}
    mill.remove();
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.reset();
  </#list>
