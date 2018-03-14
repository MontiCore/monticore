<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Write parser header -->
<#assign genHelper = glex.getGlobalVar("parserHelper")>
grammar ${ast.getName()}Antlr;
@parser::header {
<#if genHelper.isJava()>
package ${genHelper.getParserPackage()};
</#if>
<#if genHelper.isEmbeddedJavaCode()>
import de.monticore.antlr4.MCParser;
</#if>
}
@lexer::header {
<#if genHelper.isJava()>
package ${genHelper.getParserPackage()};
</#if>
}
<#if genHelper.isEmbeddedJavaCode()>
options {
superClass=MCParser;
}
</#if>

@parser::members
