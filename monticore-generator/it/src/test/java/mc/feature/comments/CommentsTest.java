/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import de.monticore.ast.ASTNode;
import mc.GeneratorIntegrationsTest;
import mc.feature.comments.commenttest._ast.ASTStart;
import mc.feature.comments.commenttest._parser.CommentTestParser;

public class CommentsTest extends GeneratorIntegrationsTest {
  
  /**
   * This Test tests if the comments are assigned correctly. 
   * 
   * @throws IOException 
   */
  @Test
  public void testComment() throws IOException {
    StringReader r = new StringReader("start /* comment 1 */ test a // comment 2 \n test b");
    
    CommentTestParser p = new CommentTestParser();    
    java.util.Optional<ASTStart> optAst =  p.parseStart(r);
    assertTrue(optAst.isPresent());
    ASTStart ast = optAst.get();
    assertEquals(false, p.hasErrors());
    assertEquals(1, ast.getAList().size());
    assertEquals(1, ast.getBList().size());
    assertEquals(1, ((ASTNode) ast.getAList().get(0)).get_PreComments().size());
    assertEquals(1, ((ASTNode) ast.getAList().get(0)).get_PostComments().size());
    assertEquals(0, ((ASTNode) ast.getBList().get(0)).get_PreComments().size());
  }
}
