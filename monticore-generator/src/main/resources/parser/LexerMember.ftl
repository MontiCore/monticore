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

protected void storeComment(){
  if (getCompiler() != null) {
    de.monticore.ast.Comment _comment = new de.monticore.ast.Comment(getText());
    de.se_rwth.commons.SourcePosition startPos = new de.se_rwth.commons.SourcePosition(_tokenStartLine, _tokenStartCharPositionInLine);
    _comment.set_SourcePositionStart(startPos);
    _comment.set_SourcePositionEnd(getCompiler().computeEndPosition(startPos, getText()));
    getCompiler().addComment(_comment);
  }
}
</#if>
}
