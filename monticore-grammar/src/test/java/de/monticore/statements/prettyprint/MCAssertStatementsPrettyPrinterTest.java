/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MCAssertStatementsPrettyPrinterTest {

  private TestMCAssertStatementsParser parser;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCAssertStatementsMill.reset();
    TestMCAssertStatementsMill.init();
    parser = TestMCAssertStatementsMill.parser();
  }

  @Test
  public void testAssertStatement() throws IOException {
    Optional<ASTAssertStatement> result = parser.parse_StringAssertStatement("assert a : b;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAssertStatement ast = result.get();

    String output = TestMCAssertStatementsMill.prettyPrint(ast, true);

    result = parser.parse_StringAssertStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }
}
