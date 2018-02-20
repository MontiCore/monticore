<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
<#assign parseRuleName = genHelper.getParseRuleName(ruleSymbol)>
<#assign astClassName = genHelper.getASTClassName(ruleSymbol)>
<#assign grammar = genHelper.getGrammarSymbol()>
<#assign parserName = grammar.getName()?cap_first>

  /** Parses a file. 
   * 
   * @param filename Name of file to parse
   * @return Resulting AST
   * @throws IOException 
   */
  public Optional<${astClassName}> parse${parseRuleName?cap_first}(String filename) throws IOException {   
    ${parserName}AntlrParser parser = create(filename);    
	${astClassName} ast;
    ast = parser.${parseRuleName}_eof().ret;
    if (parser.hasErrors()) {
      setError(true);
      return Optional.<${astClassName}> empty();
    }
    return Optional.ofNullable(ast);
  }

  /** Parses content of a Reader.
   * 
   * @param reader Reader to parse from
   * @return Resulting AST
   * @throws IOException 
   * */
  public  Optional<${astClassName}> parse${parseRuleName?cap_first}(Reader reader) throws IOException {
 	${parserName}AntlrParser parser = create(reader);
	${astClassName} ast;
    ast = parser.${parseRuleName}_eof().ret;
    if (parser.hasErrors()) {
      setError(true);
      return Optional.<${astClassName}> empty();
    }
    return Optional.ofNullable(ast);
  }
  
  /** Parses content of a String.
   * 
   * @param str String to parse from
   * @return Resulting AST
   * @throws IOException 
   * */
  public  Optional<${astClassName}> parse_String${parseRuleName?cap_first}(String str) throws IOException {
    return parse${parseRuleName?cap_first}(new StringReader(str));
  }
  
