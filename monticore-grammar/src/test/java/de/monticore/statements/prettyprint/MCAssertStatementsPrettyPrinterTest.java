/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCAssertStatementsMill.class)
public class MCAssertStatementsPrettyPrinterTest {

  @Test
  public void testAssertStatement() throws IOException {
    TestMCAssertStatementsParser parser = TestMCAssertStatementsMill.parser();
    Optional<ASTAssertStatement> result = parser.parse_StringAssertStatement("assert a : b;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAssertStatement ast = result.get();

    String output = TestMCAssertStatementsMill.prettyPrint(ast, false);

    result = parser.parse_StringAssertStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }
}
