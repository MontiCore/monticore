/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.teststringliterals.TestStringLiteralsMill;
import de.monticore.teststringliterals._parser.TestStringLiteralsParser;
import org.junit.jupiter.api.Test;
import stringliterals._ast.ASTCharLiteral;
import stringliterals._ast.ASTStringLiteral;
import stringliterals._prettyprint.StringLiteralsFullPrettyPrinter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestStringLiteralsMill.class)
public class StringLiteralsPrettyPrinterTest {

  @Test
  public void testCharLiteralEscapeSequenz() throws IOException {
    TestStringLiteralsParser parser = TestStringLiteralsMill.parser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'\"'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors(), output);
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testCharLiteral() throws IOException {
    TestStringLiteralsParser parser = TestStringLiteralsMill.parser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'c'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors(), output);
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testStringLiteral() throws IOException {
    TestStringLiteralsParser parser = TestStringLiteralsMill.parser();
    Optional<ASTStringLiteral> result = parser
        .parseStringLiteral(new StringReader("\"Text mit 893\""));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStringLiteral sliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(sliteral);
    result = parser.parseStringLiteral(new StringReader(output));
    assertFalse(parser.hasErrors(), output);
    assertTrue(result.isPresent());
    
    assertTrue(sliteral.deepEquals(result.get()));
  }
  
}
