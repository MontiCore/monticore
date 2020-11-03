/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.interfaces.sub._ast.ASTA;
import mc.feature.interfaces.sub._parser.SubParser;

public class InterfacesTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1a() throws IOException {
    
    SubParser parser = new SubParser();    
    Optional<mc.feature.interfaces.sub._ast.ASTA> ast = parser.parseA(new StringReader("Hello Otto Mustermann"));
    
    assertTrue(ast.get() instanceof ASTA);
    ASTA astA = ast.get();
    assertNotNull(astA.getB());
  }
  
}
