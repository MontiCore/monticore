/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.teststringliterals._parser.TestStringLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import prettyprint.StringLiteralsFullPrettyPrinter;
import stringliterals._ast.ASTCharLiteral;
import stringliterals._ast.ASTStringLiteral;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringLiteralsPrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testCharLiteralEscapeSequenz() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'\"'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testCharLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'c'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testStringLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTStringLiteral> result = parser
        .parseStringLiteral(new StringReader("\"Text mit 893\""));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStringLiteral sliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(sliteral);
    result = parser.parseStringLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(sliteral.deepEquals(result.get()));
  }
  
}
