<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "suffix", "methods")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
<#assign grammar = genHelper.getGrammarSymbol()>
<#assign parserName = grammar.getName()?cap_first>
<#assign startRule = genHelper.getStartRuleName()>
<#assign qualifiedStartRule = genHelper.getQualifiedStartRuleName()>
 
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

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
    return parse_String${startRule}(str);
  }
 
<#-- generate all methods -->
<#list methods as method>
  <#if genHelper.generateParserForRule(method)>
    ${tc.includeArgs("parser.MCParserMethods", [method])}
  </#if>
</#list>

}
