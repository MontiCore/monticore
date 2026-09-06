/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.*;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(FeatureDSLMill.class)
public class CommentTest {

  @Test
  public void testConstants() throws IOException  {
    FeatureDSLParser cp = FeatureDSLMill.parser();
    
    java.util.Optional<ASTAutomaton> optAst = cp.parse_StringAutomaton("""
        // Test
        /*Second*/ automaton a { // First Constant 1
        constants public ;// First Constant 2
        /*Second Constant*/ constants +; constants private; spices1 garlic pepper;	spices2 none;}
        """);
    assertTrue(optAst.isPresent());
    ASTAutomaton ast = optAst.get();
    
    // Parsing
    assertFalse(cp.hasErrors());
    assertEquals("a", ast.getName());
    
    assertTrue(((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    assertFalse(((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    assertTrue(((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    assertFalse(((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    assertFalse(((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    assertTrue(((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    assertTrue(((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    assertTrue(((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
    
    assertEquals("// Test", ast.get_PreCommentList().get(0).getText());
    assertEquals("/*Second*/", ast.get_PreCommentList().get(1).getText());
    assertEquals("// First Constant 1", ast.getWiredList().get(0).get_PreCommentList().getFirst().getText());
    assertEquals("// First Constant 2", ast.getWiredList().get(0).get_PostCommentList().getFirst().getText());
    assertEquals("/*Second Constant*/", ast.getWiredList().get(1).get_PreCommentList().getFirst().getText());
  }
  
}
