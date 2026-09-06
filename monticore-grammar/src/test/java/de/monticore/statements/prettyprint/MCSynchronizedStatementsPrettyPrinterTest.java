/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.testmcsynchronizedstatements.TestMCSynchronizedStatementsMill;
import de.monticore.statements.testmcsynchronizedstatements._parser.TestMCSynchronizedStatementsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCSynchronizedStatementsMill.class)
public class MCSynchronizedStatementsPrettyPrinterTest {

  @Test
  public void testReturnStatement() throws IOException {
    TestMCSynchronizedStatementsParser parser = TestMCSynchronizedStatementsMill.parser();
    Optional<ASTSynchronizedStatement> result = parser.parse_StringSynchronizedStatement("synchronized (foo) { final Integer foo = a ;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSynchronizedStatement ast = result.get();

    String output = TestMCSynchronizedStatementsMill.prettyPrint(ast, false);

    result = parser.parse_StringSynchronizedStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

}
