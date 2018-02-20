/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class CommentTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConstants() throws IOException  {
    StringReader s = new StringReader(
        "// Test \n /*Second*/ automaton a { // First Constant 1\n constants public ;// First Constant 2\n /*Second Constant*/ constants +; constants private; spices1 garlic pepper;	spices2 none;}");
        
    FeatureDSLParser cp = new FeatureDSLParser();
    
    java.util.Optional<ASTAutomaton> optAst = cp.parseAutomaton(s);
    ASTAutomaton ast = optAst.get();
    
    // Parsing
    assertEquals(false, cp.hasErrors());
    assertEquals("a", ast.getName());
    
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
    
    // TODO Antlr: Will man das Newline am Ende des Kommentars haben?
    assertEquals("// Test ", ast.get_PreComments().get(0).getText());
    assertEquals("/*Second*/", ast.get_PreComments().get(1).getText());
    assertEquals("// First Constant 1", ast.getWiredList().get(0).get_PreComments().get(0).getText());
    assertEquals("// First Constant 2",  ast.getWiredList().get(0).get_PostComments().get(0).getText());
    assertEquals("/*Second Constant*/", ast.getWiredList().get(1).get_PreComments().get(0).getText());
    
  }
  
}
