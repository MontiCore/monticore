<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rulecomp", "var")}
<@compress single_line=true>
    <#if rulecomp.isCommonTokenString()>
        "\"" + ${var} + "\""
    <#elseif rulecomp.isCommonTokenChar()>
        "\'" + ${var} + "\'"
    <#else>
        ${var}
    </#if>
</@compress>
