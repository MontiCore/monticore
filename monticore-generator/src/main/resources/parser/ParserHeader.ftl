<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Write parser header -->
<#assign genHelper = glex.getGlobalVar("parserHelper")>
parser grammar ${ast.getName()}ParserAntlr;
@parser::header {
<#if genHelper.isJava()>
package ${genHelper.getParserPackage()};
</#if>
<#if genHelper.isEmbeddedJavaCode()>
import de.monticore.antlr4.*;
import de.monticore.parser.*;
import ${genHelper.getQualifiedGrammarName()?lower_case}.*;
</#if>
}

<#if genHelper.isEmbeddedJavaCode()>
options {
  superClass=MCParser;
  tokenVocab=${ast.getName()}LexerAntlr;
}
</#if>

@parser::members
