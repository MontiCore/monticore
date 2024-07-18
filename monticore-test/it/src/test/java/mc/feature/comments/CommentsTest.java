/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import de.monticore.ast.ASTNode;
import mc.GeneratorIntegrationsTest;
import mc.feature.comments.commenttest._ast.ASTStart;
import mc.feature.comments.commenttest._parser.CommentTestParser;
import org.junit.jupiter.api.Test;

public class CommentsTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
    Assertions.assertTrue(optAst.isPresent());
    ASTStart ast = optAst.get();
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertEquals(1, ast.getAList().size());
    Assertions.assertEquals(1, ast.getBList().size());
    Assertions.assertEquals(1, ((ASTNode) ast.getAList().get(0)).get_PreCommentList().size());
    Assertions.assertEquals(1, ((ASTNode) ast.getAList().get(0)).get_PostCommentList().size());
    Assertions.assertEquals(0, ((ASTNode) ast.getBList().get(0)).get_PreCommentList().size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
