<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "referencedSymbol", "symbolName")}
<#assign symbol = attributeName + "Symbol">
   ${referencedSymbol} ${symbol} = get${symbol?cap_first}();

   return ${symbol}.get${symbolName}Node().get();