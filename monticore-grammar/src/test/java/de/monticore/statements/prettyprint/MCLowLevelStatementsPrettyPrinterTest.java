/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mclowlevelstatements._ast.ASTBreakStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.testmclowlevelstatements._parser.TestMCLowLevelStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCLowLevelStatementsPrettyPrinterTest  {

  private TestMCLowLevelStatementsParser parser = new TestMCLowLevelStatementsParser();

  private MCLowLevelStatementsPrettyPrinter prettyPrinter = new MCLowLevelStatementsPrettyPrinter(new IndentPrinter());

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
  public void testBreakStatement() throws IOException {
    Optional<ASTBreakStatement> result = parser.parse_StringBreakStatement("break a ;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBreakStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBreakStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLabeledStatement() throws IOException {
    Optional<ASTLabel> result = parser.parse_StringLabel("a : break foo;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLabel(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testContinueStatement() throws IOException {
    Optional<ASTContinueStatement> result = parser.parse_StringContinueStatement("continue foo;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTContinueStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringContinueStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
