/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._prettyprint.MCAssertStatementsFullPrettyPrinter;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCAssertStatementsPrettyPrinterTest {

  private TestMCAssertStatementsParser parser = new TestMCAssertStatementsParser();

  private MCAssertStatementsFullPrettyPrinter prettyPrinter= new MCAssertStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTAssertStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAssertStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
