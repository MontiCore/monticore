/* (c) https://github.com/MontiCore/monticore */

package mc.feature.embedding;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.embedding.outer.featureembedded.FeatureembeddedMill;
import mc.feature.embedding.outer.featureembedded._ast.ASTExt;
import mc.feature.embedding.outer.featureembedded._parser.FeatureembeddedParser;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter3;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(FeatureembeddedMill.class)
public class EmbeddingTest {

  private ASTOuter createAST(String s) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = FeatureembeddedMill.parser();
    
    // Parse the input expression
    Optional<ASTOuter> ast = parser.parse_StringOuter(s);
    assertTrue(ast.isPresent());
    
    return ast.get();
  }
  
  private ASTOuter3 createAST3(String s) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = FeatureembeddedMill.parser();
    
    // Parse the input expression
    Optional<ASTOuter3> ast = parser.parse_StringOuter3(s);
    assertTrue(ast.isPresent());
    
    return ast.get();
  }
  
  @Test
  public void testEmbedding() throws IOException {
    ASTOuter ast = createAST("out { test }");
    
    assertEquals("test", ((ASTExt) ast.getInner()).getInner().getName());
  }

  @Test
  public void testEmbedding3() throws IOException {
    // Embedded - optional taken
    createAST3("out  {test}  ");
  }
  
  @Test
  public void testEmbedding4() throws IOException {
    // Embedded - optional not taken
    createAST3("out");
  }
  
}
