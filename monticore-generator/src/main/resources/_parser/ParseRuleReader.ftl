<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName","astClassName", "parseRuleNameJavaCompatible")}
  ${grammarName}AntlrParser parser = create(reader);
  ${astClassName} ast;
  ast = parser.${parseRuleNameJavaCompatible}().ret;
  if (parser.hasErrors()) {
    setError(true);
    return Optional.empty();
  }
  // Check for EOF
  org.antlr.v4.runtime.Token currentToken = parser.getCurrentToken();
  if (currentToken != null && currentToken.getType() != -1) {
    setError(true);
    Log.error("Expected EOF but found token " + currentToken, parser.computeStartPosition(currentToken));
    return Optional.empty();
  }
  return Optional.of(ast);