<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
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
    if (getParserTarget().equals(MCConcreteParser.ParserExecution.NORMAL)) {
      ast = parser.${parseRuleName}().ret;
    } else {
      ast = parser.${parseRuleName}_eof().ret;
    }
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
    if (getParserTarget().equals(MCConcreteParser.ParserExecution.NORMAL)) {
      ast = parser.${parseRuleName}().ret;
    } else {
      ast = parser.${parseRuleName}_eof().ret;
    }
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
  public  Optional<${astClassName}> parseString_${parseRuleName?cap_first}(String str) throws IOException {
    return parse${parseRuleName?cap_first}(new StringReader(str));
  }
  
