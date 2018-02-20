/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class LexRulesOrderTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testOrder() throws IOException {
    FeatureDSLParser parser = new FeatureDSLParser();
    parser.parseClassProd(new StringReader("aString"));
    assertFalse(parser.hasErrors());
  }
  
}
