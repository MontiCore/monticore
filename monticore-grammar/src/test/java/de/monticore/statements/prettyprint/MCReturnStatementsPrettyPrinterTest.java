/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.testmcreturnstatements.TestMCReturnStatementsMill;
import de.monticore.statements.testmcreturnstatements._parser.TestMCReturnStatementsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCReturnStatementsMill.class)
public class MCReturnStatementsPrettyPrinterTest  {

  @Test
  public void testReturnStatement() throws IOException {
    TestMCReturnStatementsParser parser = TestMCReturnStatementsMill.parser();
    Optional<ASTReturnStatement> result = parser.parse_StringReturnStatement("return a ;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTReturnStatement ast = result.get();

    String output = TestMCReturnStatementsMill.prettyPrint(ast, false);

    result = parser.parse_StringReturnStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
