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

// an indirect start-rule which enforces EOF as the final token of the input, enforcing fully consumed inputs
mc__internal__start_rule [String rule]:
  (
    <#list genHelper.getStartRules() as ruleName>
      {$rule.equals("${ruleName}")}? ${ruleName} <#sep>|
    </#list>
<#--  The empty, error-throwing alternative unfortunatly causes duplicate sempred prediction (cf. the sempred in SuperParserTest) -->
<#-- | {notifyErrorListeners("Unknown requested start-rule " + $rule);}-->
) EOF;
