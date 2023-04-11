/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcarraystatements._prettyprint.MCArrayStatementsFullPrettyPrinter;
import de.monticore.statements.testmcarraystatements._parser.TestMCArrayStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCArrayStatementsPrettyPrinterTest {

  private TestMCArrayStatementsParser parser = new TestMCArrayStatementsParser();

  private MCArrayStatementsFullPrettyPrinter prettyPrinter = new MCArrayStatementsFullPrettyPrinter(new IndentPrinter());

  @Before
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }


  @Test
  public void testArrayInit() throws IOException {
    Optional<ASTArrayInit> result = parser.parse_StringArrayInit("{a, b, foo}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayInit ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayInit(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testArrayDeclaratorId() throws IOException {
    Optional<ASTArrayDeclaratorId> result = parser.parse_StringArrayDeclaratorId("a [] []");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayDeclaratorId ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDeclaratorId(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
