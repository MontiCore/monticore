// (c) https://github.com/MontiCore/monticore
package de.monticore.comments;

import de.monticore.javalight._ast.ASTConstDeclaration;
import de.monticore.javalight._ast.ASTJavaMethod;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should document the current comment behavior
 * Note: The location of comments has changed as of MC 7.7.0
 */
public class CommentsOnASTTest {

  @BeforeAll
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestJavaLightMill.reset();
    TestJavaLightMill.init();
  }

  TestJavaLightParser parser;

  @BeforeEach
  public void before() {
    Log.clearFindings();
    this.parser = TestJavaLightMill.parser();
  }

  @Test
  public void testComments() throws IOException {
    Optional<ASTJavaMethod> ast = parser.parse("target/resources/test/de/monticore/comments/CommentsTest.jlight");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());

    ASTMethodDeclaration m = (ASTMethodDeclaration) ast.get();

    assertEquals(1, m.get_PreCommentList().size());
    assertEquals("// (c) https://github.com/MontiCore/monticore", m.get_PreComment(0).getText());
    assertEquals(1, m.get_PreCommentList().size());
    assertEquals("// After doStuff", m.get_PostComment(0).getText());

    assertEquals(1, m.sizeMCModifiers());
    assertEquals(0, m.getMCModifier(0).get_PostCommentList().size());
    assertEquals(0, m.getMCModifier(0).get_PostCommentList().size());


    assertEquals(1, m.getMCReturnType().get_PreCommentList().size());
    assertEquals("/* t2 */", m.getMCReturnType().get_PreComment(0).getText());
    assertEquals(0, m.getMCReturnType().get_PostCommentList().size());

    assertEquals(1, m.getFormalParameters().get_PreCommentList().size());
    assertEquals("/* t4 */", m.getFormalParameters().get_PreComment(0).getText());
    assertEquals(0, m.getFormalParameters().get_PostCommentList().size());

    assertEquals(1, m.getMCJavaBlock().get_PreCommentList().size());
    assertEquals("/* t6 */", m.getMCJavaBlock().get_PreComment(0).getText());
    assertEquals(1, m.getMCJavaBlock().get_PostCommentList().size());
    assertEquals("// Final doStuff", m.getMCJavaBlock().get_PostComment(0).getText());


    ASTConstDeclaration c = (ASTConstDeclaration) m.getMCJavaBlock().getMCBlockStatement(0);
    assertEquals(1, c.get_PreCommentList().size());
    assertEquals("// First doStuff", c.get_PreComment(0).getText());
    assertEquals(2, c.get_PostCommentList().size());
    // Note: When pretty-printing /*A*/;//B ,
    // the result will look like ; /*A*/ //B
    assertEquals("/* after value */", c.get_PostComment(0).getText());
    assertEquals("// after line", c.get_PostComment(1).getText());

    assertEquals(0, c.getLocalVariableDeclaration().sizeMCModifiers());

    assertEquals(0, c.getLocalVariableDeclaration().getMCType().get_PreCommentList().size());
    assertEquals(0, c.getLocalVariableDeclaration().getMCType().get_PostCommentList().size());

    assertEquals(1, c.getLocalVariableDeclaration().getVariableDeclarator(0).get_PreCommentList().size());
    assertEquals("/* pre name */", c.getLocalVariableDeclaration().getVariableDeclarator(0).get_PreComment(0).getText());

    assertEquals(2, c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreCommentList().size());
    assertEquals("/* pre op */", c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreComment(0).getText());
    assertEquals("/* pre value */", c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreComment(1).getText());

  }
}
