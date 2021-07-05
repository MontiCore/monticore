<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
lexer grammar ${ast.getName()}LexerAntlr;

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

<#list genHelper.getLexerRulesForMode() as modeName, lexProdList>
  mode ${modeName};
  <#list lexProdList as lexProd>
    <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
      ${lexerRule}
    </#list>
  </#list>
</#list>

