// (c) https://github.com/MontiCore/monticore
package de.monticore.comments;

import de.monticore.javalight._ast.ASTConstDeclaration;
import de.monticore.javalight._ast.ASTJavaMethod;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * This test should document the current comment behavior
 * Note: some comments are not correctly transferred to the AST
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
    Assertions.assertEquals("// (c) https://github.com/MontiCore/monticore", m.get_PreCommentList().get(0).getText());

    Assertions.assertEquals(1, m.sizeMCModifiers());
    Assertions.assertEquals(1, m.getMCModifier(0).get_PostCommentList().size());
    Assertions.assertEquals("/* after doStuff:mod */", m.getMCModifier(0).get_PostCommentList().get(0).getText());

    Assertions.assertEquals(0, m.getMCReturnType().get_PostCommentList().size());

    Assertions.assertEquals(1, m.getMCReturnType().getMCVoidType().get_PostCommentList().size());
    Assertions.assertEquals("/* after doStuff:type */", m.getMCReturnType().getMCVoidType().get_PostCommentList().get(0).getText());


    Assertions.assertEquals(1, m.getFormalParameters().get_PostCommentList().size());
    Assertions.assertEquals("/* after doStuff:name */", m.getFormalParameters().get_PostCommentList().get(0).getText());

    // Missing: // First doStuff

    ASTConstDeclaration c = (ASTConstDeclaration) m.getMCJavaBlock().getMCBlockStatement(0);
    Assertions.assertEquals(0, c.getLocalVariableDeclaration().sizeMCModifiers());

    Assertions.assertEquals(4, c.getLocalVariableDeclaration().getMCType().get_PostCommentList().size());
    Assertions.assertEquals("/* after i:type */", c.getLocalVariableDeclaration().getMCType().get_PostCommentList().get(0).getText());
    Assertions.assertEquals("/* after i:name */", c.getLocalVariableDeclaration().getMCType().get_PostCommentList().get(1).getText());
    Assertions.assertEquals("/* after i:op */", c.getLocalVariableDeclaration().getMCType().get_PostCommentList().get(2).getText());
    Assertions.assertEquals("/* after i:val */", c.getLocalVariableDeclaration().getMCType().get_PostCommentList().get(3).getText());

    // Missing // Final doStuff
    // Missing // After doStuff
  }
}
