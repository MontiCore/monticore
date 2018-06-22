<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
<#assign symbol = attributeName + "Symbol">
   return get${symbol?cap_first}Opt().isPresent();