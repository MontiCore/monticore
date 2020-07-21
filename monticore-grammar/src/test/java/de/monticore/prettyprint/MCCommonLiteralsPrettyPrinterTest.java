/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCommonLiteralsPrettyPrinterTest {

  private TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();

  private MCCommonLiteralsPrettyPrinter prettyPrinter = new MCCommonLiteralsPrettyPrinter(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testIntLiteral() throws IOException {
    Optional<ASTNullLiteral> result = parser.parse_StringNullLiteral("null");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTNullLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringNullLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testFalseBooleanLiteral() throws IOException {
    Optional<ASTBooleanLiteral> result = parser.parse_StringBooleanLiteral("true");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBooleanLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBooleanLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testTrueBooleanLiteral() throws IOException {
    Optional<ASTBooleanLiteral> result = parser.parse_StringBooleanLiteral("false");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBooleanLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBooleanLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCharLiteral() throws IOException {
    Optional<ASTCharLiteral> result = parser.parse_StringCharLiteral("'c'");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCharLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testStringLiteral() throws IOException {
    Optional<ASTStringLiteral> result = parser.parse_StringStringLiteral("\"something\"");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStringLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringStringLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSignedNatLiteral() throws IOException {
    Optional<ASTSignedNatLiteral> result = parser.parse_StringSignedNatLiteral("-123456789");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSignedNatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedNatLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testBasicLongLiteral() throws IOException {
    Optional<ASTBasicLongLiteral> result = parser.parse_StringBasicLongLiteral("13745934L");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBasicLongLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicLongLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSignedBasicLongLiteral() throws IOException {
    Optional<ASTSignedBasicLongLiteral> result = parser.parse_StringSignedBasicLongLiteral("-13745934L");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSignedBasicLongLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicLongLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testBasicFloatLiteral() throws IOException {
    Optional<ASTBasicFloatLiteral> result = parser.parse_StringBasicFloatLiteral("13745934.73649F");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBasicFloatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicFloatLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSignedBasicFloatLiteral() throws IOException {
    Optional<ASTSignedBasicFloatLiteral> result = parser.parse_StringSignedBasicFloatLiteral("-13745934.38462F");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSignedBasicFloatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicFloatLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testBasicDoubleLiteral() throws IOException {
    Optional<ASTBasicDoubleLiteral> result = parser.parse_StringBasicDoubleLiteral("13745934.73649");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBasicDoubleLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBasicDoubleLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSignedBasicDoubleLiteral() throws IOException {
    Optional<ASTSignedBasicDoubleLiteral> result = parser.parse_StringSignedBasicDoubleLiteral("-13745934.38462");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSignedBasicDoubleLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSignedBasicDoubleLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
