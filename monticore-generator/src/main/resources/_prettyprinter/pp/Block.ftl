<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "blockData", "grammarName", "astPackage")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="grammarName" type="String" -->
<#-- @ftlvariable name="astPackage" type="String" -->

<@block glex blockData grammarName astPackage/>

<#macro block glex blockData grammarName astPackage>
<#if blockData.getAltDataList()?has_content && blockData.getAltDataList()?first.isAlwaysTrue() >
<#-- Simplify the Block, as only one always-true Alt is present -->
    // Simplified always true
    ${includeArgs("Alt", ast, blockData.getAltDataList()?first, grammarName, astPackage)}
    // Ignoring ${blockData.getAltDataList()?size - 1} other alt(s) (with less NonTerminals)
<#elseif blockData.isList() && blockData.getAltDataList()?size == 1>
    <#assign alt = blockData.getAltDataList()?first>
    while  ( ${alt.getExpressionConj()} )
    { // While single alt
    ${includeArgs("Alt", ast, alt, grammarName, astPackage)}
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
        { // opt: ${alt.getOptional()} req: ${alt.getRequired()}
        ${includeArgs("Alt", ast, alt, grammarName, astPackage)}
        }
        <#sep> <#if !blockData.isNotListButNoElse()>else<#else> /* noelse 1 */</#if>
    </#list>
    }

<#else >
    <#list blockData.getAltDataList() as alt>
            <#if !alt?is_last || alt.getExpressionList()?has_content> <#--> // Simplify else if (true)  </#-->
                if ( ${alt.getExpressionConj()} )
            </#if>
            { // opt: ${alt.getOptional()} req: ${alt.getRequired()}
            ${includeArgs("Alt", ast, alt, grammarName, astPackage)}
            }
            <#sep> <#if !blockData.isNotListButNoElse()>else<#else> /* noelse 2 */ </#if>
    </#list>
</#if>
</#macro>
