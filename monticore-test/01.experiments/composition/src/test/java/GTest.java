/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import g.GMill;
import g._ast.ASTA;
import g._ast.ASTB;
import g._ast.ASTC;
import g._parser.GParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(GMill.class)
public class GTest {
  
  @Test
  public  void testG() throws IOException {
    GParser p = GMill.parser();
    Optional<ASTC> ast = p.parse_String("0, \"foo\"");
    assertTrue(ast.isPresent() && ast.get() instanceof ASTA);
    
    Optional<ASTC> ast2 = p.parse_String("\"foo\": 9");
    assertTrue(ast2.isPresent() && ast2.get() instanceof ASTB);
  }
  
}
