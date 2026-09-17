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
// An array of non-keywords, used to clean up error messages
private final static String[] NOKEYWORD_RULES = new String[]{ "__obsolete_in_case_of_no_nokeywords"
<#list genHelper.getPlusNoKeywordRules() as ruleName, tokens>
   <#list tokens as token>,"${token}"</#list>
</#list>};
@Override
protected java.lang.String[] getNoKeywordRuleNames() {
  return NOKEYWORD_RULES;
}
}

// Parser-Productions
<#list genHelper.getParserRulesToGenerate() as parserProd>
  <#list antlrGenerator.createAntlrCode(parserProd) as parserRule>
  ${parserRule}
  </#list>
</#list>

// Interface-Rules
<#list genHelper.getInterfaceRulesToGenerate() as interfaceProd>
  <#list antlrGenerator.createAntlrCodeForInterface(interfaceProd) as interfaceRule>
  ${interfaceRule}
  </#list>
</#list>

// Split lex symbols
<#list genHelper.getSplitLexSymbolsWithInherited() as splitSymbol>
 ${splitSymbol}
</#list>

// __mc_plus_keywords
<#list genHelper.getPlusKeywordRules() as ruleName, tokens>
    ${ruleName} : <#list tokens as token>mc__internal__token=${token}<#sep>|</#list>;
</#list>

// __mc_incl_nokeywords - instead of next("x") as a predicate, we turn "x" into a token and create a rule-substitute for Names n:Name|x
<#list genHelper.getPlusNoKeywordRules() as ruleName, tokens>
    ${ruleName} : <#list tokens as token>mc__internal__token=${token}<#sep>|</#list>;
</#list>
