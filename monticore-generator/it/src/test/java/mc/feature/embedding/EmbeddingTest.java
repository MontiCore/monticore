/* (c) https://github.com/MontiCore/monticore */

package mc.feature.embedding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.embedding.outer.featureembedded._ast.ASTExt;
import mc.feature.embedding.outer.featureembedded._parser.FeatureembeddedParser;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter3;

public class EmbeddingTest extends GeneratorIntegrationsTest {
  
  private ASTOuter createAST(String filename, Reader r) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = new FeatureembeddedParser();
    
    // Parse the input expression
    Optional<ASTOuter> ast = parser.parseOuter(r);
    
    return ast.get();
  }
  
  private ASTOuter3 createAST3(String filename, Reader r) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = new FeatureembeddedParser();
    
    // Parse the input expression
    Optional<ASTOuter3> ast = parser.parseOuter3(r);
    
    return ast.get();
  }
  
  @Test
  public void testEmbedding() throws IOException {
    
    StringReader s = new StringReader("out { test }");
    
    ASTOuter ast = createAST("hihi", s);
    
    assertEquals("test", ((ASTExt) ast.getInner()).getInner().getName());
    
  }

  @Test
  public void testEmbedding3() throws IOException {
    
    StringReader s = new StringReader("out  {test}  ");
    
    createAST3("Embedded - optional taken", s);
    
  }
  
  @Test
  public void testEmbedding4() throws IOException {
    
    StringReader s = new StringReader("out");
    
    createAST3("Embedded - optional not taken", s);
    
  }
  
}
