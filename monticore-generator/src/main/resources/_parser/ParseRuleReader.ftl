<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "astClassName", "parseRuleNameJavaCompatible")}
  ${grammarName}AntlrParser parser = create(reader);
  ${astClassName} astPV;
  var prc = parser.mc__internal__start_rule("${parseRuleNameJavaCompatible}");
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
  // Build ast
  ${grammarName}ASTBuildVisitor buildVisitor = new ${grammarName}ASTBuildVisitor(parser.getFilename(), (org.antlr.v4.runtime.CommonTokenStream)parser.getTokenStream());
  astPV = (${astClassName})prc.${parseRuleNameJavaCompatible}().accept(buildVisitor);
  buildVisitor.addFinalComments(astPV, prc);
  return Optional.of(astPV);
