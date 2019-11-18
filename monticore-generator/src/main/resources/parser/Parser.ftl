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
      //1
  ${parserRule}
  </#list>
</#list>

<#list genHelper.getInterfaceRulesToGenerate() as interfaceProd>
  <#list antlrGenerator.createAntlrCodeForInterface(interfaceProd) as interfaceRule>
    //2
  ${interfaceRule}
  </#list>
</#list>
 
<#list genHelper.getLexSymbolsWithInherited() as lexSymbol>
  //3
  ${genHelper.getLexSymbolName(lexSymbol)} : '${lexSymbol}';
</#list>
 
<#list genHelper.getLexerRulesToGenerate() as lexProd>
  //4
  <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
  ${lexerRule}
  </#list>
</#list>

