/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.mclowlevelstatements._prettyprint.MCLowLevelStatementsFullPrettyPrinter;
import de.monticore.statements.testmclowlevelstatements.TestMCLowLevelStatementsMill;
import de.monticore.statements.testmclowlevelstatements._parser.TestMCLowLevelStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCLowLevelStatementsPrettyPrinterTest  {

  private TestMCLowLevelStatementsParser parser = new TestMCLowLevelStatementsParser();

  private MCLowLevelStatementsFullPrettyPrinter prettyPrinter = new MCLowLevelStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCLowLevelStatementsMill.reset();
    TestMCLowLevelStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testBreakStatement() throws IOException {
    Optional<ASTLabelledBreakStatement> result = parser.parse_StringLabelledBreakStatement("break a ;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLabelledBreakStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLabelledBreakStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLabeledStatement() throws IOException {
    Optional<ASTLabel> result = parser.parse_StringLabel("a : break foo;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLabel(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testContinueStatement() throws IOException {
    Optional<ASTContinueStatement> result = parser.parse_StringContinueStatement("continue foo;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTContinueStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringContinueStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
