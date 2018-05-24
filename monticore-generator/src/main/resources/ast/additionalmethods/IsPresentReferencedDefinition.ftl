<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName")}
<#assign definition = attributeName + "Definition">
   return get${definition?cap_first}Opt().isPresent();