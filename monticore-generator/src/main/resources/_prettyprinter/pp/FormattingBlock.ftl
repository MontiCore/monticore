<#-- @ftlvariable name="node" type="de.monticore.ast.ASTNode" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
<#-- @ftlvariable name="helper" type="de.monticore.codegen.prettyprint.FormattingPrettyPrinterGenerationVisitor.FormattingHelper" -->
${tc.signature("node", "blockData", "grammarName", "astPackage", "helper")}

<#list blockData.getAltDataList() as alt>
    <#if !alt?is_last || alt.getExpressionList()?has_content>
        if ( ${alt.getExpressionConj()} ) {
    <#else>
        {
    </#if>
        ${includeArgs("FormattingAlt", node, alt, grammarName, astPackage, helper)}
    }
    <#sep> else </#sep>
</#list>