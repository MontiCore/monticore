<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName")}
  ${grammarName}AntlrLexer lexer = new ${grammarName}AntlrLexer(new org.antlr.v4.runtime.ANTLRInputStream(reader));
  org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);
  ${grammarName}AntlrParser parser = new ${grammarName}AntlrParser(tokens);
  lexer.setMCParser(parser);
  lexer.removeErrorListeners();
  lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
  parser.setFilename("StringReader");
  setError(false);
  return parser;