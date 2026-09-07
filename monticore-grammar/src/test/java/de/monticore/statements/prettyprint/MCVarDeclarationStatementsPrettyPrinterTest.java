/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCVarDeclarationStatementsMill.class)
public class MCVarDeclarationStatementsPrettyPrinterTest {

  @Test
  public void testLocalVariableDeclaration() throws IOException {
    TestMCCommonStatementsParser parser = TestMCCommonStatementsMill.parser();
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("List a = b, c = d");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    String output = TestMCCommonStatementsMill.prettyPrint(ast, false);

    result = parser.parse_StringLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

}
