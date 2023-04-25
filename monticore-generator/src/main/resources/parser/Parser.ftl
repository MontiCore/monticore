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

<#list genHelper.getNoKeyordsWithInherited() as noKeyword>
  ${noKeyword}
</#list>

<#list genHelper.getSplitLexSymbolsWithInherited() as splitSymbol>
 ${splitSymbol}
</#list>

