<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  traverse initialization for a FullPrettyPrinter
  This method is overrideable
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="grammarSymbol" type="String" -->
<#-- @ftlvariable name="replacedKeywordCurrentGrammar" type="java.util.Map<de.monticore.grammar.grammar._symboltable.ProdSymbol, java.util.Map<String, java.util.Collection<String>>>" -->
${tc.signature("grammarSymbol", "replacedKeywordCurrentGrammar")}
<#assign service = glex.getGlobalVar("service")>
<#assign doGenerateReplKeywordError=false>
{
<#list replacedKeywordCurrentGrammar as prod, replacedKeywords>
    // TODO @Override handle (${prod.getName()}) to achieve replaced keywords, replace the following keywords/terminals
    <#list replacedKeywords as keyword, replacements>
        <#assign doGenerateReplKeywordError=doGenerateReplKeywordError||!replacements?seq_contains(keyword)>
    // - ${keyword} with ${replacements?join(" or ")}
    </#list>
</#list>
};
<#if doGenerateReplKeywordError>
Log.error("0xA1066${service.getGeneratedErrorCode(grammarSymbol)} replacekeyword requires HC effort for pretty printing");
</#if>
