/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astident;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.astident.astident.AstIdentMill;
import mc.feature.astident.astident._ast.ASTA;
import mc.feature.astident.astident._parser.AstIdentParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AstIdentMill.class)
public class TestASTIdent {

  @Test
  public void testParser() throws IOException {
    
    StringReader s = new StringReader(
        "Otto");
    
    AstIdentParser p = AstIdentMill.parser();
    java.util.Optional<ASTA> ast = p.parseA(s);
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    
    // Test parsing
    assertEquals("Otto", ast.get().getName());
  }
  
}
