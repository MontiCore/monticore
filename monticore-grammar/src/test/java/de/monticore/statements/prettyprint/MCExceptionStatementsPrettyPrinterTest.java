/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcexceptionstatements._ast.*;
import de.monticore.statements.mcexceptionstatements._prettyprint.MCExceptionStatementsFullPrettyPrinter;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCExceptionStatementsPrettyPrinterTest {

  private TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();

  private MCExceptionStatementsFullPrettyPrinter prettyPrinter = new MCExceptionStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testTryStatement2() throws IOException {
    Optional<ASTTryStatement2> result = parser.parse_StringTryStatement2(" try { Integer foo = a;} finally { Integer foo = a; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTTryStatement2 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement2(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTryStatement1() throws IOException {
    Optional<ASTTryStatement1> result = parser.parse_StringTryStatement1(" try { Integer foo = a; } catch (private static a.b.c | d.e.G foo) {Integer foo = a; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTTryStatement1 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement1(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testTryStatements() throws IOException {
    Optional<ASTTryStatement3> result = parser.parse_StringTryStatement3("try ( public Integer a = foo; ) " +
        "{ public String foo = a ;} " +
        "catch (private static a.b.c | d.e.G foo) { public String foo = a ;}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTTryStatement3 ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryStatement3(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testTryvariableDeclaration() throws IOException {
    Optional<ASTTryLocalVariableDeclaration> result = parser.parse_StringTryLocalVariableDeclaration("public Integer a = foo");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTTryLocalVariableDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringTryLocalVariableDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCatchClause() throws IOException {
    Optional<ASTCatchClause> result = parser.parse_StringCatchClause("catch (private static a.b.c | d.e.G foo) { public String foo = a; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCatchClause ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCatchClause(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCatchType() throws IOException {
    Optional<ASTCatchTypeList> result = parser.parse_StringCatchTypeList(" a.b.c | d.e.G ");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCatchTypeList ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCatchTypeList(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testThrowStatement() throws IOException {
    Optional<ASTThrowStatement> result = parser.parse_StringThrowStatement("throw Exception;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTThrowStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringThrowStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
