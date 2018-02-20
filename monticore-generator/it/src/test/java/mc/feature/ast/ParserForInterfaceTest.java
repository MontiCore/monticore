/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class ParserForInterfaceTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testExtraComponent() throws IOException {
    StringReader s = new StringReader("spices1 garlic ;");
    
    FeatureDSLParser p = new FeatureDSLParser();
    p.parseExtraComponent(s);
    
    assertEquals(false, p.hasErrors());
    
  }
  
}
