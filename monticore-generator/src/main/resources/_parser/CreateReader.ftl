<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName")}
  ${grammarName}AntlrLexer lexer = new ${grammarName}AntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(reader));
  org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);
  ${grammarName}AntlrParser parser = new ${grammarName}AntlrParser(tokens);
  lexer.setMCParser(parser);
  lexer.removeErrorListeners();
  lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
  parser.setFilename("StringReader");
  setError(false);
  if (!mode.isEmpty()) {
    int index = Arrays.asList(lexer.getModeNames()).indexOf(mode);
    if (index>=0) {
      lexer.mode(index);
    } else {
      Log.error("0xA01101${service.getGeneratedErrorCode(grammarName)} Invalid mode name " + mode);
    }
  }
  return parser;