/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCAssertStatementsPrettyPrinterTest {

  private TestMCAssertStatementsParser parser = new TestMCAssertStatementsParser();

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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTAssertStatement ast = result.get();

    String output = TestMCAssertStatementsMill.prettyPrint(ast, true);

    result = parser.parse_StringAssertStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
