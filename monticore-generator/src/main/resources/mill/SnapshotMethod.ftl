<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateClassName", "superSymbolList")}
${stateClassName} state = new ${stateClassName}();
state.mill = mill;
<#list superSymbolList as superSymbol>
state.${superSymbol.getName()?uncap_first}Mill = ${superSymbol.getFullName()?lower_case}.${superSymbol.getName()}Mill.getMill();
</#list>
return state;