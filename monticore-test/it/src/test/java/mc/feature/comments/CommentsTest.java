/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.comments.commenttest.CommentTestMill;
import mc.feature.comments.commenttest._ast.ASTStart;
import mc.feature.comments.commenttest._parser.CommentTestParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(CommentTestMill.class)
public class CommentsTest {

  /**
   * This Test tests if the comments are assigned correctly. 
   * 
   * @throws IOException 
   */
  @Test
  public void testComment() throws IOException {
    CommentTestParser p = CommentTestMill.parser();
    java.util.Optional<ASTStart> optAst =  p.parse_StringStart("start /* comment 1 */ test a // comment 2 \n test b");
    assertTrue(optAst.isPresent());
    ASTStart ast = optAst.get();
    assertFalse(p.hasErrors());
    assertEquals(1, ast.getAList().size());
    assertEquals(1, ast.getBList().size());
    assertEquals(1, ast.getAList().getFirst().get_PreCommentList().size());
    assertEquals(1, ast.getAList().getFirst().get_PostCommentList().size());
    assertEquals(0, ast.getBList().getFirst().get_PreCommentList().size());
  }
}
