<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "astClassName", "parseRuleNameJavaCompatible")}
  ${grammarName}AntlrParser parser = create(fileName);
  ${astClassName} ast;
  ast = parser.${parseRuleNameJavaCompatible}().ret;
  if (parser.hasErrors()) {
    setError(true);
    return Optional.empty();
  }
  parser.mc_eof();
  if (parser.hasErrors()) {
    setError(true);
    return Optional.empty();
  }
  return Optional.of(ast);
