/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.testmclowlevelstatements.TestMCLowLevelStatementsMill;
import de.monticore.statements.testmclowlevelstatements._parser.TestMCLowLevelStatementsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCLowLevelStatementsMill.class)
public class MCLowLevelStatementsPrettyPrinterTest  {

  @Test
  public void testBreakStatement() throws IOException {
    TestMCLowLevelStatementsParser parser = TestMCLowLevelStatementsMill.parser();
    Optional<ASTLabelledBreakStatement> result = parser.parse_StringLabelledBreakStatement("break a ;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLabelledBreakStatement ast = result.get();

    String output = TestMCLowLevelStatementsMill.prettyPrint(ast, true);

    result = parser.parse_StringLabelledBreakStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLabeledStatement() throws IOException {
    TestMCLowLevelStatementsParser parser = TestMCLowLevelStatementsMill.parser();
    Optional<ASTLabel> result = parser.parse_StringLabel("a : break foo;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLabel ast = result.get();

    String output = TestMCLowLevelStatementsMill.prettyPrint(ast, true);

    result = parser.parse_StringLabel(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testContinueStatement() throws IOException {
    TestMCLowLevelStatementsParser parser = TestMCLowLevelStatementsMill.parser();
    Optional<ASTContinueStatement> result = parser.parse_StringContinueStatement("continue foo;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTContinueStatement ast = result.get();

    String output = TestMCLowLevelStatementsMill.prettyPrint(ast, true);

    result = parser.parse_StringContinueStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
