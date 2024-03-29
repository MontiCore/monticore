/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._prettyprint.MCAssertStatementsFullPrettyPrinter;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCAssertStatementsPrettyPrinterTest {

  private TestMCAssertStatementsParser parser = new TestMCAssertStatementsParser();

  private MCAssertStatementsFullPrettyPrinter prettyPrinter= new MCAssertStatementsFullPrettyPrinter(new IndentPrinter());

  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCAssertStatementsMill.reset();
    TestMCAssertStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testAssertStatement() throws IOException {
    Optional<ASTAssertStatement> result = parser.parse_StringAssertStatement("assert a : b;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAssertStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAssertStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
