<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "symbolName")}
<#assign symbol = attributeName + "Symbol">
   Optional<${referencedSymbol}> ${symbol} = get${symbol?cap_first}Opt();

   if (${symbol}.isPresent()) {
     return ${symbol}.get().get${symbolName}Node();
   }

   return Optional.empty();