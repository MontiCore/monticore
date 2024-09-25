<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("suffix")}
<#-- Write parser header -->
<#assign genHelper = glex.getGlobalVar("parserHelper")>
parser grammar ${ast.getName()}AntlrParser${suffix};
@parser::header {
<#if genHelper.isEmbeddedJavaCode()>
import de.monticore.antlr4.*;
import de.monticore.parser.*;
import ${genHelper.getQualifiedGrammarName()?lower_case}.*;
</#if>
}

<#if genHelper.isEmbeddedJavaCode()>
options {
  superClass=${genHelper.getParserSuperClass()};
  tokenVocab=${ast.getName()}AntlrLexer${suffix};
}
</#if>

@parser::members
