<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "millName", "superSymbolList")}
    mill = new ${millName}();
  <#list superSymbolList as superSymbol>
    ${superSymbol.getFullName()?lower_case}._ast.${superSymbol.getName()}Mill.initMe(new ${superSymbol.getName()}MillFor${millName?remove_ending("Mill")}());
  </#list>
