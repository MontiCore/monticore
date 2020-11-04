/* (c) https://github.com/MontiCore/monticore */

package mc.feature.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTTestOptions;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class MultipleOptionTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    StringReader r = new StringReader("constants constants");
    
    FeatureDSLParser p = new FeatureDSLParser();
    
    Optional<ASTTestOptions> ast = p.parseTestOptions(r);
    
    assertEquals(false, p.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(false, ast.get().isA());
    
  }
}
