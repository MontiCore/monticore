/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConstants() throws IOException  {
    StringReader s = new StringReader(
        "// Test \n /*Second*/ automaton a { // First Constant 1\n constants public ;// First Constant 2\n /*Second Constant*/ constants +; constants private; spices1 garlic pepper;	spices2 none;}");
        
    FeatureDSLParser cp = new FeatureDSLParser();
    
    java.util.Optional<ASTAutomaton> optAst = cp.parseAutomaton(s);
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
    
    assertEquals("// Test ", ast.get_PreCommentList().get(0).getText());
    assertEquals("/*Second*/", ast.get_PreCommentList().get(1).getText());
    assertEquals("// First Constant 1", ast.getWiredList().get(0).get_PreCommentList().getFirst().getText());
    assertEquals("// First Constant 2", ast.getWiredList().get(0).get_PostCommentList().getFirst().getText());
    assertEquals("/*Second Constant*/", ast.getWiredList().get(1).get_PreCommentList().getFirst().getText());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
