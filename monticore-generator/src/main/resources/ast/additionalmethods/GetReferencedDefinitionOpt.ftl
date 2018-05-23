<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "referencedSymbol", "symbolName")}
<#assign symbol = attributeName + "Symbol">
   if (get${symbol?cap_first}Opt().isPresent()) {
     return get${symbol?cap_first}Opt().get().get${symbolName}Node();
   }
   return Optional.empty();