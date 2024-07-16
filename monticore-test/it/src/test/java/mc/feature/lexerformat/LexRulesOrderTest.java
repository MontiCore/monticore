/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class LexRulesOrderTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testOrder() throws IOException {
    FeatureDSLParser parser = new FeatureDSLParser();
    parser.parseClassProd(new StringReader("aString"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
