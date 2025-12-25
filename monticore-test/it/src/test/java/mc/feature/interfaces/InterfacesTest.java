/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.interfaces.sub._ast.ASTA;
import mc.feature.interfaces.sub._parser.SubParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    
    assertInstanceOf(ASTA.class, ast.get());
    ASTA astA = ast.get();
    assertNotNull(astA.getB());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
