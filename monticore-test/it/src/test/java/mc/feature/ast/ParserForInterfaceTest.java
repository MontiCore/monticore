/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class ParserForInterfaceTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testExtraComponent() throws IOException {
    StringReader s = new StringReader("spices1 garlic ;");
    
    FeatureDSLParser p = new FeatureDSLParser();
    p.parseExtraComponent(s);
    
    assertEquals(false, p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
