/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserForInterfaceTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testExtraComponent() throws IOException {
    StringReader s = new StringReader("spices1 garlic ;");
    
    FeatureDSLParser p = new FeatureDSLParser();
    p.parseExtraComponent(s);
    
    assertFalse(p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
