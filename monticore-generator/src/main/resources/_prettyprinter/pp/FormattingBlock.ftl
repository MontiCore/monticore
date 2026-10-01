<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("blockData", "grammarName", "astPackage")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->

<@block blockData grammarName astPackage/>

<#macro block blockData grammarName astPackage>
<#if blockData.getAltDataList()?has_content && blockData.getAltDataList()?first.isAlwaysTrue() >
<#-- Simplify the Block, as only one always-true Alt is present -->
    // Simplified always true
    ${includeArgs("_prettyprinter.pp.FormattingAlt", blockData.getAltDataList()?first, grammarName, astPackage)}
    // Ignoring ${blockData.getAltDataList()?size - 1} other alt(s) (with less NonTerminals)
<#elseif blockData.isList() && blockData.getAltDataList()?size == 1>
    <#assign alt = blockData.getAltDataList()?first>
    while  ( ${alt.getExpressionConj()} )
    { // While single alt
    ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
    }
<#elseif blockData.isList() >
    while (
    <#list blockData.getAltDataList() as alt>
        ( ${alt.getExpressionConj()} )
        <#sep> ||
    </#list>
    ) { // while multiple alt
    <#list blockData.getAltDataList() as alt>
        <#if !alt?is_last || alt.getExpressionList()?has_content> <#--> // Simplify else if (true)  </#-->
            if ( ${alt.getExpressionConj()} )
        </#if>
        { // opt: ${alt.getOptional()} req: ${alt.getRequired()} #list o:${alt.getOptionalSet()?join(",")} / r:${alt.getRequiredSet()?join(",")}
        ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
        }
        <#sep> <#if !blockData.isNotListButNoElse()>else<#else> /* noelse 1 */</#if>
    </#list>
    }

<#else >
    <#list blockData.getAltDataList() as alt>
            <#if !alt?is_last || alt.getExpressionList()?has_content> <#--> // Simplify else if (true)  </#-->
                if ( ${alt.getExpressionConj()} )
            </#if>
            { // opt: ${alt.getOptional()} req: ${alt.getRequired()} #if o:${alt.getOptionalSet()?join(",")} / r:${alt.getRequiredSet()?join(",")}
            ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
            }
            <#sep> <#if !blockData.isNotListButNoElse()>else<#else> /* noelse 2 */ </#if>
    </#list>
</#if>
</#macro>