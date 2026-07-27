<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superSymbolList")}
mill = state.mill;
<#list superSymbolList as superSymbol>
${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.reset();
${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.initMe(state.${superSymbol.getName()?uncap_first}Mill);
</#list>