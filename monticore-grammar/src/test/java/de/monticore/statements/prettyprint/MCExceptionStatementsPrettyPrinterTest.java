package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcexceptionstatements._ast.*;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCExceptionStatementsPrettyPrinterTest {

  private TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();

  private MCExceptionStatementsPrettyPrinterDelegator prettyPrinter = new MCExceptionStatementsPrettyPrinterDelegator(new IndentPrinter());

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
  public void testTryStatement() throws IOException {
    Optional<ASTTryStatement> result = parser.parse_StringTryStatement(" try { private Integer foo = a } finally { public String foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCatchExceptionsHandler() throws IOException {
    Optional<ASTCatchExceptionsHandler> result = parser.parse_StringCatchExceptionsHandler("catch (private static a.b.c | d.e.G foo) { public String foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCatchExceptionsHandler ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCatchExceptionsHandler(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFinallyBlockOnlyHandler() throws IOException {
    Optional<ASTFinallyBlockOnlyHandler> result = parser.parse_StringFinallyBlockOnlyHandler("finally { public String foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFinallyBlockOnlyHandler ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFinallyBlockOnlyHandler(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testTryStatementWithResources() throws IOException {
    Optional<ASTTryStatementWithResources> result = parser.parse_StringTryStatementWithResources("try ( public Integer a = foo; ) " +
        "{ public String foo = a } " +
        "catch (private static a.b.c | d.e.G foo) { public String foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryStatementWithResources ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatementWithResources(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testResource() throws IOException {
    Optional<ASTResource> result = parser.parse_StringResource("public Integer a = foo");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTResource ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringResource(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testCatchClause() throws IOException {
    Optional<ASTCatchClause> result = parser.parse_StringCatchClause("catch (private static a.b.c | d.e.G foo) { public String foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCatchClause ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCatchClause(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testCatchType() throws IOException {
    Optional<ASTCatchTypeList> result = parser.parse_StringCatchTypeList(" a.b.c | d.e.G ");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCatchTypeList ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCatchTypeList(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testThrowStatement() throws IOException {
    Optional<ASTThrowStatement> result = parser.parse_StringThrowStatement("throw Exception;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTThrowStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringThrowStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
