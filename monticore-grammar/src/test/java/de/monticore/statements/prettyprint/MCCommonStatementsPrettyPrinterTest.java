/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTArrayInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTDeclaratorId;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCommonStatementsPrettyPrinterTest {

  private TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();

  private MCCommonStatementsPrettyPrinterDelegator prettyPrinter= new MCCommonStatementsPrettyPrinterDelegator(new IndentPrinter());

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
  public void testBlock() throws IOException {
    Optional<ASTMCJavaBlock> result = parser.parse_StringMCJavaBlock("{ private Integer foo = a; }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCJavaBlock ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMCJavaBlock(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testBlockStatement() throws IOException {
    Optional<ASTMCBlockStatement> result = parser.parse_StringMCBlockStatement("private Integer foo = a;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCBlockStatement ast = result.get();

    ast.accept(prettyPrinter);
    String output = prettyPrinter.getPrinter().getContent();

    result = parser.parse_StringMCBlockStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLocalVariableDeclaration() throws IOException {
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("private Integer foo = a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testVariableDeclaration() throws IOException {
    Optional<ASTVariableDeclarator> result = parser.parse_StringVariableDeclarator("foo = a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTVariableDeclarator ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringVariableDeclarator(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testDeclaratorId() throws IOException {
    Optional<ASTDeclaratorId> result = parser.parse_StringDeclaratorId("a [][][]");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDeclaratorId ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDeclaratorId(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testArrayInitializer() throws IOException {
    Optional<ASTArrayInit> result = parser.parse_StringArrayInit("{a, b, foo}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayInit ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayInit(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testIfStatement() throws IOException {
    Optional<ASTIfStatement> result = parser.parse_StringIfStatement("if(a) ; else ;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIfStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIfStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testForStatement() throws IOException {
    Optional<ASTForStatement> result = parser.parse_StringForStatement("for(i; i ; i) a;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTForStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringForStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCommonForControl() throws IOException {
    Optional<ASTCommonForControl> result = parser.parse_StringCommonForControl("i; i ; i");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCommonForControl ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCommonForControl(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testForInitByExpressions() throws IOException {
    Optional<ASTForInitByExpressions> result = parser.parse_StringForInitByExpressions("i, b, c");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTForInitByExpressions ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringForInitByExpressions(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testWhileStatement() throws IOException {
    Optional<ASTWhileStatement> result = parser.parse_StringWhileStatement("while (a) ;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTWhileStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringWhileStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testDoWhileStatement() throws IOException {
    Optional<ASTDoWhileStatement> result = parser.parse_StringDoWhileStatement("do ; while (a);");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDoWhileStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDoWhileStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSwitchStatement() throws IOException {
    Optional<ASTSwitchStatement> result = parser.parse_StringSwitchStatement("switch (a) {case b : c; default : d;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSwitchStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSwitchStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testEmptyStatement() throws IOException {
    Optional<ASTEmptyStatement> result = parser.parse_StringEmptyStatement(";");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTEmptyStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEmptyStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testExpressionStatement() throws IOException {
    Optional<ASTExpressionStatement> result = parser.parse_StringExpressionStatement("a;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTExpressionStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringExpressionStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSwitchBlockStatementGroup() throws IOException {
    Optional<ASTSwitchBlockStatementGroup> result = parser.parse_StringSwitchBlockStatementGroup("case a: foo;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSwitchBlockStatementGroup ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSwitchBlockStatementGroup(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstantExpressionSwitchLabel() throws IOException {
    Optional<ASTConstantExpressionSwitchLabel> result = parser.parse_StringConstantExpressionSwitchLabel("case a :");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstantExpressionSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstantExpressionSwitchLabel(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testEnumConstantSwitchLabel() throws IOException {
    Optional<ASTEnumConstantSwitchLabel> result = parser.parse_StringEnumConstantSwitchLabel("case a :");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTEnumConstantSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEnumConstantSwitchLabel(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testDefaultSwitchLabel() throws IOException {
    Optional<ASTDefaultSwitchLabel> result = parser.parse_StringDefaultSwitchLabel("default :");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDefaultSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDefaultSwitchLabel(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
