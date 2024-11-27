<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator","suffix")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
lexer grammar ${ast.getName()}AntlrLexer${suffix};

<#if genHelper.getLexerSuperClass()??>
options {
  superClass = ${genHelper.getLexerSuperClass()};
}
</#if>

${tc.includeArgs("parser.LexerMember", [antlrGenerator, parserHelper.getGrammarSymbol().getName()])}

// Implicit token
<#list genHelper.getLexSymbolsWithInherited() as lexSymbol>
  ${genHelper.getLexSymbolName(lexSymbol)} : '${lexSymbol}';
</#list>

// Explicit token
<#list genHelper.getLexerRulesToGenerate() as lexProd>
  <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
  ${lexerRule}
  </#list>
</#list>

// nokeyword rules (will be added to the name__including_all_keywords)
<#assign alreadyAddedImplicit=genHelper.getLexSymbolsWithInherited()>
<#list genHelper.getNoKeywordNameAlts() as t>
    <#if !alreadyAddedImplicit?seq_contains(t)>
      ${genHelper.getLexSymbolName(t)}: '${t}';
    </#if>
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

