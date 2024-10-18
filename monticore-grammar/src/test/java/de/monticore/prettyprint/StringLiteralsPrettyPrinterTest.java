/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.teststringliterals.TestStringLiteralsMill;
import de.monticore.teststringliterals._parser.TestStringLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stringliterals._ast.ASTCharLiteral;
import stringliterals._ast.ASTStringLiteral;
import stringliterals._prettyprint.StringLiteralsFullPrettyPrinter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringLiteralsPrettyPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestStringLiteralsMill.reset();
    TestStringLiteralsMill.init();
  }
  
  @Test
  public void testCharLiteralEscapeSequenz() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'\"'"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors(), output);
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(cliteral.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCharLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'c'"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors(), output);
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(cliteral.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStringLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTStringLiteral> result = parser
        .parseStringLiteral(new StringReader("\"Text mit 893\""));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTStringLiteral sliteral = result.get();
    
    StringLiteralsFullPrettyPrinter prettyPrinter = new StringLiteralsFullPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(sliteral);
    result = parser.parseStringLiteral(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors(), output);
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(sliteral.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
