<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName","astClassName", "parseRuleNameJavaCompatible")}
  ${grammarName}AntlrParser parser = create(reader);
  ${astClassName} ast;
  ast = parser.${parseRuleNameJavaCompatible}_eof().ret;
  if (parser.hasErrors()) {
    setError(true);
    return Optional.empty();
  }
  return Optional.of(ast);