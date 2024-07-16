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

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

public class CommentTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testConstants() throws IOException  {
    StringReader s = new StringReader(
        "// Test \n /*Second*/ automaton a { // First Constant 1\n constants public ;// First Constant 2\n /*Second Constant*/ constants +; constants private; spices1 garlic pepper;	spices2 none;}");
        
    FeatureDSLParser cp = new FeatureDSLParser();
    
    java.util.Optional<ASTAutomaton> optAst = cp.parseAutomaton(s);
    ASTAutomaton ast = optAst.get();
    
    // Parsing
    Assertions.assertEquals(false, cp.hasErrors());
    Assertions.assertEquals("a", ast.getName());
    
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    Assertions.assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    Assertions.assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    Assertions.assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
    
    Assertions.assertEquals("// Test ", ast.get_PreCommentList().get(0).getText());
    Assertions.assertEquals("/*Second*/", ast.get_PreCommentList().get(1).getText());
    Assertions.assertEquals("// First Constant 1", ast.getWiredList().get(0).get_PreCommentList().get(0).getText());
    Assertions.assertEquals("// First Constant 2", ast.getWiredList().get(0).get_PostCommentList().get(0).getText());
    Assertions.assertEquals("/*Second Constant*/", ast.getWiredList().get(1).get_PreCommentList().get(0).getText());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
