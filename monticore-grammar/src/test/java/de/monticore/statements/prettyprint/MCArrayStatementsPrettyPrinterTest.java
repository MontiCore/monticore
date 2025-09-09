/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcarraystatements._prettyprint.MCArrayStatementsFullPrettyPrinter;
import de.monticore.statements.testmcarraystatements.TestMCArrayStatementsMill;
import de.monticore.statements.testmcarraystatements._parser.TestMCArrayStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCArrayStatementsPrettyPrinterTest {

  private TestMCArrayStatementsParser parser = new TestMCArrayStatementsParser();

  private MCArrayStatementsFullPrettyPrinter prettyPrinter = new MCArrayStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCArrayStatementsMill.reset();
    TestMCArrayStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }


  @Test
  public void testArrayInit() throws IOException {
    String input = "{a, b, foo}";
    Optional<ASTArrayInit> result = parser.parse_StringArrayInit(input);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTArrayInit ast = result.get();

    String output = prettyPrinter.prettyprint(ast);
    Assertions.assertEquals(input.replaceAll(" ",  ""), output.replaceAll(" ", "").replaceAll("\n", ""));

    result = parser.parse_StringArrayInit(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testArrayDeclaratorId() throws IOException {
    Optional<ASTArrayDeclaratorId> result = parser.parse_StringArrayDeclaratorId("a [] []");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTArrayDeclaratorId ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDeclaratorId(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
