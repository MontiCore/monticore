<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "referencedSymbol", "symbolName")}
<#assign symbol = attributeName + "Definition">
   return get${symbol?cap_first}Opt().get();