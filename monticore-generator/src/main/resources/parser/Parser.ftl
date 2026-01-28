<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator", "suffix")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>

${tc.includeArgs("parser.ParserHeader",suffix)}
{
// HWParserJavaCode
<#list antlrGenerator.getHWParserJavaCode() as javaCode>
  ${javaCode}
</#list>

// Idents
<#list genHelper.getIdentsToGenerate() as ident>
  ${genHelper.getConvertFunction(ident)}
</#list>  
}

// StartRule
mc__internal_startrule :
<#list genHelper.getRulesForStartRules() as startRuleSymbol>
  ( MC__INTERNAL_START_TOKEN_${antlrGenerator.getRuleNameForAntlr(startRuleSymbol.getName())?upper_case} ${antlrGenerator.getRuleNameForAntlr(startRuleSymbol.getName())})
    <#sep> |
</#list>
 EOF;

// ParserRulesToGenerate
<#list genHelper.getParserRulesToGenerate() as parserProd>
  <#list antlrGenerator.createAntlrCode(parserProd) as parserRule>
  ${parserRule}
  </#list>
</#list>

// InterfaceRulesToGenerate
<#list genHelper.getInterfaceRulesToGenerate() as interfaceProd>
  <#list antlrGenerator.createAntlrCodeForInterface(interfaceProd) as interfaceRule>
  ${interfaceRule}
  </#list>
</#list>

// NoKeywordsWithInherited
<#list genHelper.getNoKeyordsWithInherited() as noKeyword>
  ${noKeyword}
</#list>

// SplitLexSymbolsWithInherited
<#list genHelper.getSplitLexSymbolsWithInherited() as splitSymbol>
 ${splitSymbol}
</#list>

