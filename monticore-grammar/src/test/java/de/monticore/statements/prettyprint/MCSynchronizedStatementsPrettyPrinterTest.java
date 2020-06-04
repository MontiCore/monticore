/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.testmcreturnstatements._parser.TestMCReturnStatementsParser;
import de.monticore.statements.testmcsynchronizedstatements._parser.TestMCSynchronizedStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCSynchronizedStatementsPrettyPrinterTest {

  private TestMCSynchronizedStatementsParser parser = new TestMCSynchronizedStatementsParser();

  private MCSynchronizedStatementsPrettyPrinterDelegator prettyPrinter = new MCSynchronizedStatementsPrettyPrinterDelegator(new IndentPrinter());

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
  public void testReturnStatement() throws IOException {
    Optional<ASTSynchronizedStatement> result = parser.parse_StringSynchronizedStatement("synchronized (foo) { private Integer foo = a ;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSynchronizedStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSynchronizedStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

}
