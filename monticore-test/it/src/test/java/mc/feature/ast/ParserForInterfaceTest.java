/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(FeatureDSLMill.class)
public class ParserForInterfaceTest {

  @Test
  public void testExtraComponent() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    p.parse_StringExtraComponent("spices1 garlic ;");
    
    assertFalse(p.hasErrors());
  }
  
}
