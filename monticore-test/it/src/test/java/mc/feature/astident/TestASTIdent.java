/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astident;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.astident.astident._ast.ASTA;
import mc.feature.astident.astident._parser.AstIdentParser;
import org.junit.jupiter.api.Test;

public class TestASTIdent extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParser() throws IOException {
    
    StringReader s = new StringReader(
        "Otto");
    
    AstIdentParser p = new AstIdentParser();
    java.util.Optional<ASTA> ast = p.parseA(s);
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(false, p.hasErrors());
    
    // Test parsing
    Assertions.assertEquals("Otto", ast.get().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
