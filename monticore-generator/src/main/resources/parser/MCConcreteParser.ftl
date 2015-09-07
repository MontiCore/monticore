<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalValue("parserHelper")>
<#assign mCParserName = genHelper.getMCParserWrapperName(ruleSymbol)>
<#assign parseRuleName = genHelper.getParseRuleName(ruleSymbol)>
<#assign astClassName = genHelper.getASTClassName(ruleSymbol)>
<#assign grammar = genHelper.getGrammarSymbol()>
<#assign parserName = grammar.getSimpleName()>
package ${genHelper.getParserPackage()};

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import de.monticore.antlr4.MCConcreteParser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import java.util.Optional;


/**
 *  A MontiCoreMCConcreteParser wraps a rule within a grammar to 
 *  unify interface to calling .parse() for all rules
 */
public class ${mCParserName}MCParser extends MCConcreteParser {
    
  /** Parses a file. 
   * 
   * @param filename Name of file to parse
   * @return Resulting AST
   * @throws IOException 
   * @throws RecognitionException
   */
  public Optional<${astClassName}> parse(String filename) throws IOException, RecognitionException {   
    ${parserName?cap_first}Lexer lexer = new ${parserName?cap_first}Lexer(new  ANTLRFileStream(filename));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ${parserName?cap_first}Parser parser = new ${parserName?cap_first}Parser(tokens);
    lexer.setMCParser(parser);  
    parser.setFilename(filename);
    
    ${astClassName} ast = parse(parser);
    if (parser.hasErrors()) {
      hasErrors = true;
      return Optional.<${astClassName}> empty();
    }
    return Optional.ofNullable(ast);
  }
  
  /** Parses content of a Reader.
   * 
   * @param reader Reader to parse from
   * @return Resulting AST
   * @throws IOException 
   * @throws RecognitionException
   * */
  public  Optional<${astClassName}> parse(Reader reader) throws IOException, RecognitionException {
    ${parserName}Lexer lexer = new ${parserName}Lexer(new ANTLRInputStream(reader));
    CommonTokenStream tokens = new CommonTokenStream(lexer);    
    ${parserName}Parser parser = new ${parserName}Parser(tokens);
    lexer.setMCParser(parser);   
    parser.setFilename("Reader");
       
    ${astClassName} ast = parse(parser);
    if (parser.hasErrors()) {
      hasErrors = true;
      return Optional.<${astClassName}> empty();
    }
    return Optional.ofNullable(ast);
  }
  
  /** Parses content of a String.
   * 
   * @param parseString String to parse from
   * @return Resulting AST
   * @throws IOException
   * @throws RecognitionException
   *
   */
  public  Optional<${astClassName}> parseString(String parseString) throws IOException, RecognitionException {
    return parse(new StringReader(parseString));
  }
  
  /** Creates the MCParser
   * 
   */
  protected ${mCParserName}MCParser() {
    super();
  }
  
  
  /** Start parsing
   * 
   * 
   */
  protected ${astClassName} parse(${parserName?cap_first}Parser parser) throws RecognitionException {
    
    ${astClassName} ret;
    
    if (this.getParserTarget().equals(ParserExecution.NORMAL)){
      ret =  parser.${parseRuleName}().ret;
    }
    else {
      ret= parser.${parseRuleName}_eof().ret;
    }
   
    return ret;
    
  }
  
}
