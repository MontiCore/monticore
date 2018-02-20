<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("antlrGenerator", "parserName")}

@lexer::members {


<#list antlrGenerator.getHWLexerJavaCode() as javaCode>
  ${javaCode}
</#list>

<#if antlrGenerator.embeddedJavaCode>
private ${parserName}AntlrParser _monticore_parser;
protected ${parserName}AntlrParser getCompiler() {
   return _monticore_parser;
}
public void setMCParser(${parserName}AntlrParser in) {
  this._monticore_parser = in;
}
</#if>
}
