/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._prettyprint.MCReturnStatementsFullPrettyPrinter;
import de.monticore.statements.testmcreturnstatements.TestMCReturnStatementsMill;
import de.monticore.statements.testmcreturnstatements._parser.TestMCReturnStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCReturnStatementsPrettyPrinterTest  {

  private TestMCReturnStatementsParser parser = new TestMCReturnStatementsParser();

  private MCReturnStatementsFullPrettyPrinter prettyPrinter = new MCReturnStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCReturnStatementsMill.reset();
    TestMCReturnStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testReturnStatement() throws IOException {
    Optional<ASTReturnStatement> result = parser.parse_StringReturnStatement("return a ;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTReturnStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringReturnStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
