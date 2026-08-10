/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(FeatureDSLMill.class)
public class LexRulesOrderTest {

  @Test
  public void testOrder() throws IOException {
    FeatureDSLParser parser = FeatureDSLMill.parser();
    parser.parse_StringClassProd("aString");
    assertFalse(parser.hasErrors());
  }
  
}
