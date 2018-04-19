<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "symbolClass")}
<#assign symbol = attributeName + "Symbol">
    return get${symbol?cap_first}Opt().get();