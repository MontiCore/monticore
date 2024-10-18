/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsFullPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCommonLiteralsPrettyPrinterTest {

  private TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();

  private MCCommonLiteralsFullPrettyPrinter prettyPrinter = new MCCommonLiteralsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testIntLiteral() throws IOException {
    Optional<ASTNullLiteral> result = parser.parse_StringNullLiteral("null");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTNullLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringNullLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testFalseBooleanLiteral() throws IOException {
    Optional<ASTBooleanLiteral> result = parser.parse_StringBooleanLiteral("true");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBooleanLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBooleanLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testTrueBooleanLiteral() throws IOException {
    Optional<ASTBooleanLiteral> result = parser.parse_StringBooleanLiteral("false");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBooleanLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBooleanLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCharLiteral() throws IOException {
    Optional<ASTCharLiteral> result = parser.parse_StringCharLiteral("'c'");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCharLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCharLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStringLiteral() throws IOException {
    Optional<ASTStringLiteral> result = parser.parse_StringStringLiteral("\"something\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTStringLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringStringLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignedNatLiteral() throws IOException {
    Optional<ASTSignedNatLiteral> result = parser.parse_StringSignedNatLiteral("-123456789");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSignedNatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedNatLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBasicLongLiteral() throws IOException {
    Optional<ASTBasicLongLiteral> result = parser.parse_StringBasicLongLiteral("13745934L");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBasicLongLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicLongLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignedBasicLongLiteral() throws IOException {
    Optional<ASTSignedBasicLongLiteral> result = parser.parse_StringSignedBasicLongLiteral("-13745934L");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSignedBasicLongLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicLongLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBasicFloatLiteral() throws IOException {
    Optional<ASTBasicFloatLiteral> result = parser.parse_StringBasicFloatLiteral("13745934.73649F");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBasicFloatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicFloatLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignedBasicFloatLiteral() throws IOException {
    Optional<ASTSignedBasicFloatLiteral> result = parser.parse_StringSignedBasicFloatLiteral("-13745934.38462F");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSignedBasicFloatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicFloatLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBasicDoubleLiteral() throws IOException {
    Optional<ASTBasicDoubleLiteral> result = parser.parse_StringBasicDoubleLiteral("13745934.73649");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBasicDoubleLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicDoubleLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignedBasicDoubleLiteral() throws IOException {
    Optional<ASTSignedBasicDoubleLiteral> result = parser.parse_StringSignedBasicDoubleLiteral("-13745934.38462");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSignedBasicDoubleLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicDoubleLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
