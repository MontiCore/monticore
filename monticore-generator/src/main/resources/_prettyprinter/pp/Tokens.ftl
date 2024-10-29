<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rulecomp", "var")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="rulecomp" type="de.monticore.codegen.prettyprint.data.PPGuardComponent" -->
<#-- @ftlvariable name="var" type="String" -->
<@compress single_line=true>
    <#if rulecomp.isCommonTokenString()>
        "\"" + ${var} + "\""
    <#elseif rulecomp.isCommonTokenChar()>
        "\'" + ${var} + "\'"
    <#else>
        ${var}
    </#if>
</@compress>
