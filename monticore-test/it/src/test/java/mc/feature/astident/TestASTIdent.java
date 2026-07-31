/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astident;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;

import mc.GeneratorIntegrationsTest;
import mc.feature.astident.astident._ast.ASTA;
import mc.feature.astident.astident._parser.AstIdentParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestASTIdent extends GeneratorIntegrationsTest {
  
  @Test
  public void testParser() throws IOException {
    
    StringReader s = new StringReader(
        "Otto");
    
    AstIdentParser p = new AstIdentParser();
    java.util.Optional<ASTA> ast = p.parseA(s);
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    
    // Test parsing
    assertEquals("Otto", ast.get().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
