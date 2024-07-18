/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.interfaces.sub._ast.ASTA;
import mc.feature.interfaces.sub._parser.SubParser;
import org.junit.jupiter.api.Test;

public class InterfacesTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1a() throws IOException {
    
    SubParser parser = new SubParser();    
    Optional<mc.feature.interfaces.sub._ast.ASTA> ast = parser.parseA(new StringReader("Hello Otto Mustermann"));
    
    Assertions.assertTrue(ast.get() instanceof ASTA);
    ASTA astA = ast.get();
    Assertions.assertNotNull(astA.getB());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
