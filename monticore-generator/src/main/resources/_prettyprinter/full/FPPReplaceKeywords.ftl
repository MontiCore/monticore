<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  traverse initialization for a FullPrettyPrinter
  This method is overrideable
-->
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
if(true)throw new IllegalStateException("replacekeyword requires HC effort for pretty printing");
<#-- we throw an exception in case error handling is muted, but due to unreachable statements otherwise the if (true) is added-->
</#if>
