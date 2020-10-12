<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "millName", "superSymbolList", "fullDefinitionName")}
    mill = new ${millName}();
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.initMe(new ${fullDefinitionName?lower_case}.${superSymbol.getName()}MillFor${millName?remove_ending("Mill")}());
  </#list>
