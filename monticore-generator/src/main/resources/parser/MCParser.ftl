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
${tc.signature("ast", "suffix", "methods")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
<#assign grammar = genHelper.getGrammarSymbol()>
<#assign parserName = grammar.getName()?cap_first>
<#assign startRule = genHelper.getStartRuleName()>
<#assign qualifiedStartRule = genHelper.getQualifiedStartRuleName()>
 
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getParserPackage()};

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import java.util.Optional;
import de.monticore.antlr4.MCConcreteParser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ${ast.getName()}Parser${suffix} extends MCConcreteParser {

  protected ${parserName}AntlrParser create(String filename) throws IOException {
    ${parserName}AntlrLexer lexer = new ${parserName}AntlrLexer(new  ANTLRFileStream(filename, StandardCharsets.UTF_8.name()));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ${parserName}AntlrParser parser = new ${parserName}AntlrParser(tokens);
    lexer.setMCParser(parser);  
    parser.setFilename(filename);
    setError(false);
    return parser;
  }
  
  protected ${parserName}AntlrParser create(Reader reader) throws IOException {
    ${parserName}AntlrLexer lexer = new ${parserName}AntlrLexer(new ANTLRInputStream(reader));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ${parserName}AntlrParser parser = new ${parserName}AntlrParser(tokens);
    lexer.setMCParser(parser);  
    parser.setFilename("StringReader");
    setError(false);
    return parser;
  }
  
  /**
   * @see de.monticore.antlr4.MCConcreteParser#parse(java.lang.String)
   */
  @Override
  public Optional<${qualifiedStartRule}> parse(String fileName) throws IOException {
    return parse${startRule}(fileName);
  }
  
  /**
   * @see de.monticore.antlr4.MCConcreteParser#parse(java.io.Reader)
   */
  @Override
  public Optional<${qualifiedStartRule}> parse(Reader reader) throws IOException {
    return parse${startRule}(reader);
  }
  
  public Optional<${qualifiedStartRule}> parse_String(String str) throws IOException {
    return parseString_${startRule}(str);
  }
 
<#-- generate all methods -->
<#list methods as method>
  <#if genHelper.generateParserForRule(method)>
    ${tc.includeArgs("parser.MCParserMethods", [method])}
  </#if>
</#list>

}
