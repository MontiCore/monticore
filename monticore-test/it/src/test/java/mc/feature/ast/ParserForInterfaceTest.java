/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

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
    
    Assertions.assertEquals(false, p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
