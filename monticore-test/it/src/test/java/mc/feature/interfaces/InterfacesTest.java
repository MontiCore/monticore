/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.interfaces.sub.SubMill;
import mc.feature.interfaces.sub._ast.ASTA;
import mc.feature.interfaces.sub._parser.SubParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SubMill.class)
public class InterfacesTest {

  @Test
  public void test1a() throws IOException {
    
    SubParser parser = SubMill.parser();
    Optional<mc.feature.interfaces.sub._ast.ASTA> ast = parser.parse_StringA("Hello Otto Mustermann");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTA.class, ast.get());
    ASTA astA = ast.get();
    assertNotNull(astA.getB());
  }
  
}
