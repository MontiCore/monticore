/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.testmcarraystatements.TestMCArrayStatementsMill;
import de.monticore.statements.testmcarraystatements._parser.TestMCArrayStatementsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCArrayStatementsMill.class)
public class MCArrayStatementsPrettyPrinterTest {

  private TestMCArrayStatementsParser parser;

  @BeforeEach
  public void init() {
    parser = TestMCArrayStatementsMill.parser();
  }


  @Test
  public void testArrayInit() throws IOException {
    String input = "{a, b, foo}";
    Optional<ASTArrayInit> result = parser.parse_StringArrayInit(input);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayInit ast = result.get();

    String output = TestMCArrayStatementsMill.prettyPrint(ast, false);
    assertEquals(input.replace(" ",  ""), output.replace(" ", "").replace("\n", ""));

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

    String output = TestMCArrayStatementsMill.prettyPrint(ast, false);

    result = parser.parse_StringArrayDeclaratorId(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
