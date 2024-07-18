/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsFullPrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTDeclaratorId;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCommonStatementsPrettyPrinterTest {

  private TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();

  private MCCommonStatementsFullPrettyPrinter prettyPrinter= new MCCommonStatementsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testBlock() throws IOException {
    Optional<ASTMCJavaBlock> result = parser.parse_StringMCJavaBlock("{ private Integer foo = a; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCJavaBlock ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMCJavaBlock(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBlockStatement() throws IOException {
    Optional<ASTMCBlockStatement> result = parser.parse_StringMCBlockStatement("private Integer foo = a;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCBlockStatement ast = result.get();

    ast.accept(prettyPrinter.getTraverser());
    String output = prettyPrinter.getPrinter().getContent();

    result = parser.parse_StringMCBlockStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLocalVariableDeclaration() throws IOException {
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("private Integer foo = a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLocalVariableDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVariableDeclaration() throws IOException {
    Optional<ASTVariableDeclarator> result = parser.parse_StringVariableDeclarator("foo = a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTVariableDeclarator ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringVariableDeclarator(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeclaratorId() throws IOException {
    Optional<ASTDeclaratorId> result = parser.parse_StringDeclaratorId("a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTDeclaratorId ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDeclaratorId(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIfStatement() throws IOException {
    Optional<ASTIfStatement> result = parser.parse_StringIfStatement("if(a) ; else ;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTIfStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIfStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testForStatement() throws IOException {
    Optional<ASTForStatement> result = parser.parse_StringForStatement("for(i; i ; i) a;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTForStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringForStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCommonForControl() throws IOException {
    Optional<ASTCommonForControl> result = parser.parse_StringCommonForControl("i; i ; i");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTCommonForControl ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCommonForControl(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnhancedForControl() throws IOException {
    Optional<ASTEnhancedForControl> result = parser.parse_StringEnhancedForControl("protected List l : a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTEnhancedForControl ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEnhancedForControl(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFormalParameter() throws IOException {
    Optional<ASTFormalParameter> result = parser.parse_StringFormalParameter("public float f");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameter(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testForInitByExpressions() throws IOException {
    Optional<ASTForInitByExpressions> result = parser.parse_StringForInitByExpressions("i, b, c");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTForInitByExpressions ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringForInitByExpressions(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWhileStatement() throws IOException {
    Optional<ASTWhileStatement> result = parser.parse_StringWhileStatement("while (a) ;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTWhileStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringWhileStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDoWhileStatement() throws IOException {
    Optional<ASTDoWhileStatement> result = parser.parse_StringDoWhileStatement("do ; while (a);");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTDoWhileStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDoWhileStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSwitchStatement() throws IOException {
    Optional<ASTSwitchStatement> result = parser.parse_StringSwitchStatement("switch (a) {case b : c; default : d;}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSwitchStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSwitchStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEmptyStatement() throws IOException {
    Optional<ASTEmptyStatement> result = parser.parse_StringEmptyStatement(";");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTEmptyStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEmptyStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExpressionStatement() throws IOException {
    Optional<ASTExpressionStatement> result = parser.parse_StringExpressionStatement("a;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTExpressionStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringExpressionStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSwitchBlockStatementGroup() throws IOException {
    Optional<ASTSwitchBlockStatementGroup> result = parser.parse_StringSwitchBlockStatementGroup("case a: foo;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTSwitchBlockStatementGroup ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSwitchBlockStatementGroup(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstantExpressionSwitchLabel() throws IOException {
    Optional<ASTConstantExpressionSwitchLabel> result = parser.parse_StringConstantExpressionSwitchLabel("case a :");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTConstantExpressionSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstantExpressionSwitchLabel(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnumConstantSwitchLabel() throws IOException {
    Optional<ASTEnumConstantSwitchLabel> result = parser.parse_StringEnumConstantSwitchLabel("case a :");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTEnumConstantSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEnumConstantSwitchLabel(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultSwitchLabel() throws IOException {
    Optional<ASTDefaultSwitchLabel> result = parser.parse_StringDefaultSwitchLabel("default :");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTDefaultSwitchLabel ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDefaultSwitchLabel(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBreakStatement() throws IOException {
    Optional<ASTBreakStatement> result = parser.parse_StringBreakStatement("break ;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTBreakStatement ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringBreakStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
