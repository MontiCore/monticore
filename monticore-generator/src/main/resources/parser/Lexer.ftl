<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator","suffix")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
lexer grammar ${ast.getName()}AntlrLexer${suffix};

@lexer::header {
<#if genHelper.isJava()>
  package ${genHelper.getParserPackage()};
</#if>
}

${tc.includeArgs("parser.LexerMember", [antlrGenerator, parserHelper.getGrammarSymbol().getName()])}

<#list genHelper.getLexSymbolsWithInherited() as lexSymbol>
  ${genHelper.getLexSymbolName(lexSymbol)} : '${lexSymbol}';
</#list>
 
<#list genHelper.getLexerRulesToGenerate() as lexProd>
  <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
  ${lexerRule}
  </#list>
</#list>

<#assign modeMap=genHelper.getLexerRulesForMode()>
<#list modeMap as modeName, lexProdList>
  <#if modeMap?size != 1 || modeName != "DEFAULT_MODE">
  mode ${modeName};
  </#if>
  <#list lexProdList as lexProd>
    <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
      ${lexerRule}
    </#list>
  </#list>
</#list>

