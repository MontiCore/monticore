/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class TrailingSpacesPrettyPrinterTest {

  TestMCCommonStatementsParser parser;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    parser = TestMCCommonStatementsMill.parser();
  }

  @Test
  public void testSingleLineCommentEOL() throws IOException {
    // Scenario: An expression is string-concatenated into a statement as the initial value
    // First, we have to extract the inner AST node with the comment
    Optional<ASTMCJavaBlock> blocks = parser.parse_StringMCJavaBlock("{int i1 = a // single line comment\n; int i2 = b; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(blocks.isPresent());
    ASTLocalVariableDeclarationStatement setStatement = (ASTLocalVariableDeclarationStatement) blocks.get().getMCBlockStatementList().get(0);
    ASTExpression initialValueAST = ((ASTSimpleInit) setStatement.getLocalVariableDeclaration().getVariableDeclarator(0).getVariableInit()).getExpression();
    Assertions.assertEquals(1, initialValueAST.get_PostCommentList().size());

    // print the initial value expression
    String initialValue = TestMCCommonStatementsMill.prettyPrint(initialValueAST, true);
    // and ensure the pretty printed string ends with a linebreak
    Assertions.assertTrue(initialValue.endsWith("\n"));

    // then test to parse this value inlined - in case no linebreak is present, this will fail
    Optional<ASTMCJavaBlock> statement = parser.parse_StringMCJavaBlock("{int i = " + initialValue + "; int i2 = 0; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(statement.isPresent());
  }

}
