/* (c) https://github.com/MontiCore/monticore */
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
  public void testTryStatement2() throws IOException {
    Optional<ASTTryStatement2> result = parser.parse_StringTryStatement2(" try { Integer foo = a;} finally { Integer foo = a; }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryStatement2 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement2(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testTryStatement1() throws IOException {
    Optional<ASTTryStatement1> result = parser.parse_StringTryStatement1(" try { Integer foo = a; } catch (private static a.b.c | d.e.G foo) {Integer foo = a; }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryStatement1 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement1(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testTryStatements() throws IOException {
    Optional<ASTTryStatement3> result = parser.parse_StringTryStatement3("try ( public Integer a = foo; ) " +
        "{ public String foo = a ;} " +
        "catch (private static a.b.c | d.e.G foo) { public String foo = a ;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryStatement3 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement3(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testTryvariableDeclaration() throws IOException {
    Optional<ASTTryLocalVariableDeclaration> result = parser.parse_StringTryLocalVariableDeclaration("public Integer a = foo");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTryLocalVariableDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testCatchClause() throws IOException {
    Optional<ASTCatchClause> result = parser.parse_StringCatchClause("catch (private static a.b.c | d.e.G foo) { public String foo = a; }");
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
