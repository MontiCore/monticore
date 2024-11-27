<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator", "suffix")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>

${tc.includeArgs("parser.ParserHeader",suffix)}
{
<#list antlrGenerator.getHWParserJavaCode() as javaCode>
  ${javaCode}
</#list>

<#list genHelper.getIdentsToGenerate() as ident>
  ${genHelper.getConvertFunction(ident)}
</#list>
}

<#list genHelper.getParserRulesToGenerate() as parserProd>
  <#list antlrGenerator.createAntlrCode(parserProd) as parserRule>
  ${parserRule}
  </#list>
</#list>

<#list genHelper.getInterfaceRulesToGenerate() as interfaceProd>
  <#list antlrGenerator.createAntlrCodeForInterface(interfaceProd) as interfaceRule>
  ${interfaceRule}
  </#list>
</#list>

name__including_no_keywords: r__mc__identifier=Name
<#list genHelper.getNoKeywordNameAlts() as a>
  | r__mc__identifier=${genHelper.getLexSymbolName(a)}
</#list>
;

name__including_all_keywords: r__mc__identifier=Name
<#list genHelper.getNoKeywordNameAlts() as a>
  | r__mc__identifier=${genHelper.getLexSymbolName(a)}
</#list>
// and now inherited, implicit tokens (real keywords) as well
<#list genHelper.getLexSymbolsWithInherited() as a>
  <#if genHelper.isIdentifier('Name', a)>
  | r__mc__identifier=${genHelper.getLexSymbolName(a)}
  </#if>
</#list>
;

<#list genHelper.getNoKeyordsWithInherited() as noKeyword>
  ${noKeyword}
</#list>

<#list genHelper.getSplitLexSymbolsWithInherited() as splitSymbol>
 ${splitSymbol}
</#list>

