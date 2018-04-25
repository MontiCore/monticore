<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "referencedSymbol", "symbolName")}
<#assign definition = attributeName + "Definition">
   return get${definition?cap_first}Opt().isPresent();