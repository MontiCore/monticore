/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astident;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.astident.astident._ast.ASTA;
import mc.feature.astident.astident._parser.AstIdentParser;

public class TestASTIdent extends GeneratorIntegrationsTest {
  
  @Before
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
    assertTrue(ast.isPresent());
    assertEquals(false, p.hasErrors());
    
    // Test parsing
    assertEquals("Otto", ast.get().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
