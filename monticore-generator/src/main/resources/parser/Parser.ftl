<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>

${tc.include("parser.ParserHeader")}
{
<#list antlrGenerator.getHWParserJavaCode() as javaCode>
  ${javaCode}
</#list>

<#list genHelper.getIdentsToGenerate() as ident>
  ${genHelper.getConvertFunction(ident)}
</#list>  
}

${tc.includeArgs("parser.LexerMember", [antlrGenerator, parserHelper.getGrammarSymbol().getName()])}

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
 
<#list genHelper.getLexSymbolsWithInherited() as lexSymbol>
  ${genHelper.getLexSymbolName(lexSymbol)} : '${lexSymbol}';
</#list>
 
<#list genHelper.getLexerRulesToGenerate() as lexProd>
  <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
  ${lexerRule}
  </#list>
</#list>

