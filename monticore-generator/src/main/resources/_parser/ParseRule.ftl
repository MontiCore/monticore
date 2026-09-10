<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "astClassName", "parseRuleNameJavaCompatible")}
  ${grammarName}AntlrParser parser = create(fileName);
  ${astClassName} astPV;
  var prc = parser.${parseRuleNameJavaCompatible}();
  if (parser.hasErrors()) {
    setError(true);
    return Optional.empty();
  }
  // Check for EOF
  org.antlr.v4.runtime.Token currentToken = parser.getCurrentToken();
  if (currentToken != null && currentToken.getType() != -1) {
    setError(true);
    parser.setContext(prc); // revert context by one level (to indicate the used rule)
    parser.notifyErrorListeners(parser.getCurrentToken(), "mismatched input '" + currentToken.getText() + "', expected EOF", new org.antlr.v4.runtime.InputMismatchException(parser));
    return Optional.empty();
  }
  // Build ast
  ${grammarName}ASTBuildVisitor buildVisitor = new ${grammarName}ASTBuildVisitor(parser.getFilename(), (org.antlr.v4.runtime.CommonTokenStream)parser.getTokenStream());
  astPV = (${astClassName})prc.accept(buildVisitor);
  buildVisitor.addFinalComments(astPV, prc);
  return Optional.of(astPV);
