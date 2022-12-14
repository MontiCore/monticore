/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._parser.TestMCVarDeclarationStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCVarDeclarationStatementsPrettyPrinterTest {

  private TestMCVarDeclarationStatementsParser parser = new TestMCVarDeclarationStatementsParser();

  private MCVarDeclarationStatementsFullPrettyPrinter prettyPrinter = new MCVarDeclarationStatementsFullPrettyPrinter(new IndentPrinter());

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
  public void testLocalVariableDeclaration() throws IOException {
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("List a = b, c = d");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    ast.accept(prettyPrinter.getTraverser());
    String output = prettyPrinter.getPrinter().getContent();

    result = parser.parse_StringLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
