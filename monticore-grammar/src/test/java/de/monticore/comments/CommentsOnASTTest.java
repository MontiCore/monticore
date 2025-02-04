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
    Optional<ASTJavaMethod> ast = parser.parse("src/test/resources/de/monticore/comments/CommentsTest.jlight");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());

    ASTMethodDeclaration m = (ASTMethodDeclaration) ast.get();

    Assertions.assertEquals(1, m.get_PreCommentList().size());
    Assertions.assertEquals("// (c) https://github.com/MontiCore/monticore", m.get_PreComment(0).getText());
    Assertions.assertEquals(1, m.get_PreCommentList().size());
    Assertions.assertEquals("// After doStuff", m.get_PostComment(0).getText());

    Assertions.assertEquals(1, m.sizeMCModifiers());
    Assertions.assertEquals(0, m.getMCModifier(0).get_PostCommentList().size());
    Assertions.assertEquals(0, m.getMCModifier(0).get_PostCommentList().size());


    Assertions.assertEquals(1, m.getMCReturnType().get_PreCommentList().size());
    Assertions.assertEquals("/* t2 */", m.getMCReturnType().get_PreComment(0).getText());
    Assertions.assertEquals(0, m.getMCReturnType().get_PostCommentList().size());

    Assertions.assertEquals(1, m.getFormalParameters().get_PreCommentList().size());
    Assertions.assertEquals("/* t4 */", m.getFormalParameters().get_PreComment(0).getText());
    Assertions.assertEquals(0, m.getFormalParameters().get_PostCommentList().size());

    Assertions.assertEquals(1, m.getMCJavaBlock().get_PreCommentList().size());
    Assertions.assertEquals("/* t6 */", m.getMCJavaBlock().get_PreComment(0).getText());
    Assertions.assertEquals(1, m.getMCJavaBlock().get_PostCommentList().size());
    Assertions.assertEquals("// Final doStuff", m.getMCJavaBlock().get_PostComment(0).getText());


    ASTConstDeclaration c = (ASTConstDeclaration) m.getMCJavaBlock().getMCBlockStatement(0);
    Assertions.assertEquals(1, c.get_PreCommentList().size());
    Assertions.assertEquals("// First doStuff", c.get_PreComment(0).getText());
    Assertions.assertEquals(2, c.get_PostCommentList().size());
    // Note: When pretty-printing /*A*/;//B ,
    // the result will look like ; /*A*/ //B
    Assertions.assertEquals("/* after value */", c.get_PostComment(0).getText());
    Assertions.assertEquals("// after line", c.get_PostComment(1).getText());

    Assertions.assertEquals(0, c.getLocalVariableDeclaration().sizeMCModifiers());

    Assertions.assertEquals(0, c.getLocalVariableDeclaration().getMCType().get_PreCommentList().size());
    Assertions.assertEquals(0, c.getLocalVariableDeclaration().getMCType().get_PostCommentList().size());

    Assertions.assertEquals(1, c.getLocalVariableDeclaration().getVariableDeclarator(0).get_PreCommentList().size());
    Assertions.assertEquals("/* pre name */", c.getLocalVariableDeclaration().getVariableDeclarator(0).get_PreComment(0).getText());

    Assertions.assertEquals(2, c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreCommentList().size());
    Assertions.assertEquals("/* pre op */", c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreComment(0).getText());
    Assertions.assertEquals("/* pre value */", c.getLocalVariableDeclaration().getVariableDeclarator(0)
            .getVariableInit().get_PreComment(1).getText());

  }
}
