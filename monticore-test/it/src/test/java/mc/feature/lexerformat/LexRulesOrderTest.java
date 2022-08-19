/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertFalse;
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

public class LexRulesOrderTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testOrder() throws IOException {
    FeatureDSLParser parser = new FeatureDSLParser();
    parser.parseClassProd(new StringReader("aString"));
    assertFalse(parser.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
